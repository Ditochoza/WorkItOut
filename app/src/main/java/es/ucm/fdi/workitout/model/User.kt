package es.ucm.fdi.workitout.model

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    @get: Exclude var email: String = "",
    var name: String = "",
    @get: Exclude var tempPassword: String = "",
    @get: Exclude var tempPasswordValidate: String = "",
    @get: Exclude var routines: List<Routine> = emptyList(),
    @get: Exclude var routinesScheduled: List<Routine> = emptyList(),
) : Parcelable