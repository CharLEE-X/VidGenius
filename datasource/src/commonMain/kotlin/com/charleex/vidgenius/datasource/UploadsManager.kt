package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.YtVideo
import com.charleex.vidgenius.datasource.feature.youtube.YoutubeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface UploadsManager {
    val ytVideos: StateFlow<List<YtVideo>>
    val isFetchingUploads: StateFlow<Boolean>

    fun fetchUploads()
    fun signOut()
}

internal class UploadsManagerImpl(
    private val logger: Logger,
    private val youtubeRepository: YoutubeRepository,
    private val scope: CoroutineScope,
) : UploadsManager {
    override val ytVideos: StateFlow<List<YtVideo>>
        get() = youtubeRepository.flowOfYtVideos()
            .stateIn(scope, SharingStarted.WhileSubscribed(), emptyList())

    private val _isFetchingUploads = MutableStateFlow(true)
    override val isFetchingUploads: StateFlow<Boolean>
        get() = _isFetchingUploads.asStateFlow()

    init {
        _isFetchingUploads.value = false
    }

    override fun fetchUploads() {
        logger.d("Fetching uploads")
        scope.launch {
            _isFetchingUploads.value = true
            youtubeRepository.fetchUploads()
            _isFetchingUploads.value = false
        }
    }

    override fun signOut() {
        youtubeRepository.signOut()
    }
}
