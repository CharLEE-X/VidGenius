package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.datasource.feature.video.VideoRepository
import com.charleex.vidgenius.datasource.feature.youtube.YoutubeRepository
import com.charleex.vidgenius.datasource.model.LocalVideo
import com.charleex.vidgenius.datasource.model.ProgressState
import com.charleex.vidgenius.datasource.model.toYouTubeItem
import com.charleex.vidgenius.datasource.model.toYoutubeVideo
import com.charleex.vidgenius.datasource.utils.DateTimeService
import com.charleex.vidgenius.datasource.utils.UuidProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

interface VideoService {
    val isFetchingUploads: StateFlow<Boolean>
    val videos: StateFlow<List<Video>>
    val message: StateFlow<String?>

    fun getVideo(videoId: String): StateFlow<Video>
    fun startFetchingUploads()
    fun stopFetchingUploads()
    fun getVideoDetails(video: Video)

    fun addLocalVideos(files: List<*>)
    fun deleteLocalVideo(video: Video)

    fun startProcessingAllVideos(videos: List<Video>)
    fun stopProcessingVideo(video: LocalVideo)

    suspend fun updateVideo(video: Video)

    fun signOut()
}

internal class VideoServiceImpl(
    private val logger: Logger,
    private val videoProcessing: VideoProcessing,
    private val youtubeRepository: YoutubeRepository,
    private val videoRepository: VideoRepository,
    private val configManager: ConfigManager,
    private val uuidProvider: UuidProvider,
    private val dateTimeService: DateTimeService,
    private val scope: CoroutineScope,
) : VideoService {
    companion object {
        private const val MAX_RETRIES = 3
    }

    private var fetchUploadsJob: Job? = null
    private var processingAllJob: Job? = null

    override val isFetchingUploads: StateFlow<Boolean>
        get() = MutableStateFlow(fetchUploadsJob?.isActive == true).asStateFlow()

    override val videos: StateFlow<List<Video>> = flowOfVideos()
        .stateIn(scope, SharingStarted.WhileSubscribed(), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    override val message: StateFlow<String?> = _message.asStateFlow()

    override fun getVideo(videoId: String): StateFlow<Video> {
        return videoProcessing.getVideoByIdFlow(videoId)
            .stateIn(scope, SharingStarted.WhileSubscribed(), videoProcessing.getVideoById(videoId))
    }

    override fun startFetchingUploads() {
        logger.d("Starting fetching uploads")
        val secretsFile = configManager.config.value.ytConfig?.secretsFile
            ?: error("No yt config found")
        scope.launch {
            supervisorScope {
                fetchUploadsJob = launch {
                    youtubeRepository.fetchUploads(secretsFile).collect { youTubeItems ->
                        logger.d("Fetched ${youTubeItems.size} uploads")

                        val videosWithLocalVideos = videos.value.filter { it.localVideo != null }

                        val ytVideos = youTubeItems.map { it.toYoutubeVideo() }

                        val ytVideosHavingLocalVideo = ytVideos.filter { ytVideo ->
                            videosWithLocalVideos.any { video ->
                                video.localVideo?.name == ytVideo.title
                            }
                        }

                        ytVideos // Existing Videos with LocalVideo's
                            .mapNotNull { ytVideo ->
                                val video =
                                    videosWithLocalVideos.find { video ->
                                        video.localVideo?.name == ytVideo.title
                                    }
                                video?.copy(ytVideo = ytVideo)
                            }
                            .also { logger.d("Existing videos: ${it.size}") }
                            .forEach {
                                videoRepository.updateVideo(it)
                                getVideoDetails(it)
                            }

                        ytVideos // New Videos
                            .filter { it !in ytVideosHavingLocalVideo }
                            .also { logger.d("New videos: ${it.size}") }
                            .map {
                                Video(
                                    id = uuidProvider.uuid(),
                                    ytVideo = it,
                                    progressState = ProgressState.Queued,
                                    localVideo = null,
                                    createdAt = dateTimeService.nowInstant(),
                                    modifiedAt = dateTimeService.nowInstant(),
                                )
                            }
                            .forEach {
                                videoRepository.createVideo(it)
                                getVideoDetails(it)
                            }
                    }
                    logger.d("Finished fetching uploads")
                }
            }
        }
    }

    override fun stopFetchingUploads() {
        logger.d("Stopping fetching uploads")
        fetchUploadsJob?.cancel()
        fetchUploadsJob = null
    }

    override fun getVideoDetails(video: Video) {
        scope.launch {
            if (video.ytVideo == null) {
                logger.d("Video is null")
                return@launch
            }

            val ytConfig = configManager.config.value.ytConfig
            if (ytConfig == null) {
                logger.d("YtConfig is null")
                return@launch
            }

            val youTubeItem = youtubeRepository.getVideoExtendedDetails(
                video.ytVideo.toYouTubeItem(),
                ytConfig.secretsFile
            )
            val newVideo = video.copy(ytVideo = youTubeItem.toYoutubeVideo())
            videoRepository.updateVideo(newVideo)
        }
    }

    override fun addLocalVideos(files: List<*>) {
        logger.d("Adding videos $files")
        scope.launch {
            videoProcessing.addLocalVideos(files)
        }
    }

    override fun deleteLocalVideo(video: Video) {
        logger.d("Deleting video ${video.localVideo?.path}")
        videoProcessing.deleteLocalVideo(video)
    }

    override fun startProcessingAllVideos(videos: List<Video>) {
        logger.d("Processing all videos $videos")
        scope.launch {
            supervisorScope {
                val jobs = videos.map { video ->
                    // Start each processVideo coroutine asynchronously
                    async {
                        if (video.ytVideo == null) {
                            _message.update { "Error while processing ${video.id} | YtVideo not found" }
                            return@async
                        }

                        if (video.localVideo == null) {
                            _message.update { "Error while processing ${video.id} | LocalVideo not found" }
                            return@async
                        }

                        processVideo(video)
                    }
                }

                awaitAll(*jobs.toTypedArray())
            }
        }
    }

    override fun stopProcessingVideo(video: LocalVideo) {
        processingAllJob?.cancel()
        processingAllJob = null
    }

    override suspend fun updateVideo(video: Video) {
        scope.launch {
//                val ytVideo = youtubeRepository.ytVideosFlow().first()
//                    .firstOrNull { it.id == video.ytVideoId }
//                    ?: run {
//                        logger.d("Yt video not found for ${video.id}")
//                        return@launch
//                    }
//                val localVideo = videoProcessor.flowOfVideos().first()
//                    .firstOrNull { it.id == video.localVideoId }
//                    ?: run {
//                        logger.d("Local video not found for ${video.id}")
//                        return@launch
//                    }
//                val result = youtubeRepository.updateVideo(
//                    ytVideo = ytVideo,
//                    localVideo = localVideo,
//                )
//                if (result) {
//                    logger.d("Updated video ${video.id} | $result")
//                } else {
//                    logger.d("Error while updating video ${video.id} | $result")
//                }
        }
    }

    override fun signOut() {
        youtubeRepository.signOut()
    }

    private suspend fun processVideo(video: Video) {
        var tryIndex = 0
        var isCompleted = false

        while (tryIndex < MAX_RETRIES || !isCompleted) {
            try {
                videoProcessing.processVideoToScreenshots(
                    video = video,
                    numberOfScreenshots = 3,
                    onError = { msg ->
                        _message.update { msg }
                    },
                )
                isCompleted = true
            } catch (e: Exception) {
                e.printStackTrace()
                tryIndex++

//                _message.update { "Error while processing ${ytVideo.id} | Retrying $tryIndex" }
//                videoProcessor.processVideo(
//                    ytVideo = ytVideo,
//                    localVideo = localVideo,
//                    numberOfScreenshots = 3,
//                    onError = {
//                        _message.update { it }
//                    }
//                )
            } finally {
//                if (isCompleted) {
//                    _message.update { "Finished processing ${ytVideo.id}" }
//                } else {
//                    _message.update { "Error while processing ${ytVideo.id} | Failed after $MAX_RETRIES tries" }
//                }
            }
        }
    }

    private fun flowOfVideos(): Flow<List<Video>> {
        return combine(
            videoProcessing.flowOfVideos(),
            configManager.config
        ) { videos, config ->
            videos.filter { video ->
                video.ytVideo?.privacyStatus in config.selectedPrivacyStatuses
            }
        }
            .map { it.sortedByDescending { it.ytVideo?.publishedAt } }
            .onEach { logger.d("Videos: ${it.size}") }
    }
}
