package src.charleex.autoytvid.repository

import co.touchlab.kermit.Logger
import com.charleex.autoytvid.yt.ChannelUploadsService
import com.charleex.autoytvid.yt.model.ChannelUploadsItem
import kotlinx.datetime.Instant

interface YoutubeRepository {
    suspend fun getChannelUploads(): List<UploadItem>
    suspend fun getVideoDetail(videoId: String): UploadItem
}

internal class YoutubeRepositoryImpl(
    private val logger: Logger,
    private val channelUploadsService: ChannelUploadsService,
) : YoutubeRepository {
    override suspend fun getChannelUploads(): List<UploadItem> {
        logger.d { "Getting channel uploads" }
        return channelUploadsService.getUploadList().toUploadItems()
    }

    override suspend fun getVideoDetail(videoId: String): UploadItem {
        logger.d { "Getting video detail $videoId" }
        return channelUploadsService.getVideoDetail(videoId).toUploadItem()
    }
}

private fun List<ChannelUploadsItem>.toUploadItems(): List<UploadItem> {
    return map { it.toUploadItem() }
}

private fun ChannelUploadsItem.toUploadItem(): UploadItem {
    return UploadItem(
        id = this.videoId,
        title = this.title,
        description = this.description,
        publishedAt = this.publishedAt,
    )
}

data class UploadItem(
    val id: String,
    val title: String,
    val description: String,
    val publishedAt: Instant,
)
