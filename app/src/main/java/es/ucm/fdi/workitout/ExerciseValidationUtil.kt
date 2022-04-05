package es.ucm.fdi.workitout

import android.net.Uri
import android.view.View
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout

object ExerciseValidationUtil {

    fun validateExercise(
        image: Pair<Uri, Pair<TextView,TextView>>,
        name: Pair<String, TextInputLayout>,
        description: Pair<String, TextInputLayout>,
        muscles: Pair<List<String>,TextView>
    ) : Boolean {

        var error = false

        if(image.first == Uri.EMPTY){
            error = true
            image.second.first.visibility = View.GONE
            image.second.second.visibility = View.VISIBLE
        }

        if (name.first.isEmpty()) {
            error = true
            name.second.error = "You must introduce a name"
        }else if (name.first.isNotEmpty()){
            if(name.first.length > 35){
                error = true
                name.second.error = "The name is too long"
            }else if (name.first.length < 5){
                error = true
                name.second.error = "The name is too short"
            }
        }

        if (description.first.isEmpty()) {
            error = true
            description.second.error = "You must introduce a description"
        }else if (description.first.isNotEmpty()){
            if (description.first.length > 175) {
                error = true
                description.second.error = "The decription is too long"
            }else if(description.first.length < 10){
                error = true
                description.second.error = "The decription is too short"
            }
        }

        if(muscles.first.isEmpty()){
            error = true
            muscles.second.visibility = View.VISIBLE
        }


        return error
    }

    fun validateVideoLink(videoLink: Pair<String,TextInputLayout>): Boolean{
        var error: Boolean = false

        if(videoLink.first.isNotEmpty() && !videoLink.first.contains("https://www.youtube.com/watch?v=", ignoreCase = true)){
            error = true
            videoLink.second.error = "video url is malformed"
        }

        return error
    }

}