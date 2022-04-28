package es.ucm.fdi.workitout.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecordLog(
    var pos: Int = -1,
    var repsLogged: Int = 0,
    var weightLogged: Int = 0,
    var timeLogged: Int = 0
):Parcelable
