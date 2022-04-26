package es.ucm.fdi.workitout.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecordLog(
    var pos: Int = -1,
    var repsLogged: Int = -1,
    var weightLogged: Int = -1,
    var timeLogged: Int = -1
):Parcelable
