package es.ucm.fdi.workitout.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Video(
    var url: String = "",
    var title: String = "",
    var description: String = "",
    var thumbnail: String = "",
    var videoLink: VideoLink = VideoLink()
): Parcelable