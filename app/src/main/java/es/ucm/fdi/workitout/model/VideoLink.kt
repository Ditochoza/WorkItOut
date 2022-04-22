package es.ucm.fdi.workitout.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoLink(
    var idUser: String = "",
    var videoUrl: String = ""
): Parcelable
