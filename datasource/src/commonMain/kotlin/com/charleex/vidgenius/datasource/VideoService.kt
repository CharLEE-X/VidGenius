package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.datasource.feature.youtube.YoutubeRepository
import com.charleex.vidgenius.datasource.model.LocalVideo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

interface VideoService {
    val isFetchingUploads: StateFlow<Boolean>
    val videos: StateFlow<List<Video>>
    val message: StateFlow<String?>

    fun startFetchingUploads()
    fun stopFetchingUploads()

    fun addLocalVideos(files: List<*>)
    fun deleteLocalVideo(video: Video)

    fun startProcessingAllVideos(videos: List<Video>)
    fun stopProcessingVideo(video: LocalVideo)

    suspend fun updateVideo(video: Video)

    fun signOut()
}

internal class VideoServiceImpl(
    private val logger: Logger,
    private val videoProcessor: VideoProcessing,
    private val youtubeRepository: YoutubeRepository,
    private val configManager: ConfigManager,
    private val scope: CoroutineScope,
) : VideoService {
    companion object {
        private const val MAX_RETRIES = 3
    }

    private var fetchUploadsJob: Job? = null
    private var processingAllJob: Job? = null

    override val isFetchingUploads: StateFlow<Boolean>
        get() = MutableStateFlow(fetchUploadsJob?.isActive == true)

    override val videos: StateFlow<List<Video>>
        get() = emptyFlow<List<Video>>()
            .stateIn(scope, SharingStarted.WhileSubscribed(), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    override val message: StateFlow<String?> = _message.asStateFlow()

    override fun startFetchingUploads() {
        logger.d("Starting fetching uploads")
        scope.launch {
            supervisorScope {
                val secretsFile = configManager.config.value.ytConfig?.secretsFile
                    ?: error("No yt config found")
                fetchUploadsJob = launch {
                    youtubeRepository.fetchUploads(secretsFile)
                }
            }
        }
    }

    override fun stopFetchingUploads() {
        logger.d("Stopping fetching uploads")
        fetchUploadsJob?.cancel()
        fetchUploadsJob = null
    }

    override fun addLocalVideos(files: List<*>) {
        logger.d("Adding videos $files")
        scope.launch {
            videoProcessor.addLocalVideos(files)
        }
    }

    override fun deleteLocalVideo(video: Video) {
        logger.d("Deleting video ${video.localVideo?.path}")
        videoProcessor.deleteLocalVideo(video)
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
                videoProcessor.processVideoToScreenshots(
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
}
