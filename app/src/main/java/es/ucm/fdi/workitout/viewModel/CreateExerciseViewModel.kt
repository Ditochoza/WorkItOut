package es.ucm.fdi.workitout.viewModel

import android.content.DialogInterface
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import es.ucm.fdi.workitout.model.*
import es.ucm.fdi.workitout.repository.YoutubeAPI
import es.ucm.fdi.workitout.utils.ValidationExerciseUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateExerciseViewModel(private var savedStateHandle: SavedStateHandle): ViewModel() {

    private val _tempExercise = MutableStateFlow(savedStateHandle.get(::tempExercise.name) ?: Exercise())
    val tempExercise: StateFlow<Exercise> = _tempExercise.asStateFlow()

    private val yotubeAPI = YoutubeAPI()

    private val _videoList = MutableStateFlow(emptyList<Video>())
    val videoList: StateFlow<List<Video>> = _videoList.asStateFlow()

    init {
        getVideoData()
    }

    fun getVideoData(){
        //Vaciamos la lista de links
        _videoList.value = emptyList<Video>()
        viewModelScope.launch {
            tempExercise.value.videoLinks.forEach { vlinkObj ->
                val vUrl = vlinkObj.videoUrl
                val videoResult = yotubeAPI.getVideoInfo(vUrl)
                if (videoResult is DatabaseResult.Success) videoResult.data?.let { video ->

                    var videoData = _videoList.value.toMutableList()
                    videoData.add(video)
                    _videoList.value = videoData

                }else{
                    var videoOffline = Video(url=vUrl,title=vUrl)
                    var videoData = _videoList.value.toMutableList()
                    videoData.add(videoOffline)
                    _videoList.value = videoData
                }
            }

        }
    }
    fun createExercise(ivExercise: ImageView, tvImageError: TextView, tilName: TextInputLayout,
                       tilDescription: TextInputLayout, tvMusclesError: TextView, sModel: MainSharedViewModel
    ) {
        val result = ValidationExerciseUtil.validateExercise(
            ivExercise to tvImageError,
            tempExercise.value.name to tilName,
            tempExercise.value.description to tilDescription,
            tempExercise.value.muscles to tvMusclesError
        )

        if(result is ValidationResult.Success){
            sModel.createExercise(ivExercise, tempExercise.value)
        }
    }

    //para no añadir links repetidos
    var existe:Boolean = false

    fun addVideoLink(etVideolink: EditText,tilVideolink: TextInputLayout) {
        existe = false
        val vlink = etVideolink.text.toString()

        val error = ValidationExerciseUtil.validateVideoLink(
            vlink to tilVideolink,
        )



        videoList.value.forEach { video ->
            if(video.url == vlink) {
                existe = true
            }
        }

        if(existe){
            Toast.makeText(tilVideolink.context.applicationContext,"Video already added!",Toast.LENGTH_SHORT).show()
        }

        if(!error && !existe){
            viewModelScope.launch {
                val videoResult = yotubeAPI.getVideoInfo(vlink)
                if (videoResult is DatabaseResult.Success) videoResult.data?.let { video ->

                    var videoData = _videoList.value.toMutableList()
                    videoData.add(video)
                    _videoList.value = videoData

                    var videoLinks = tempExercise.value.videoLinks.toMutableList()
                    videoLinks.add(video.videoLink)
                    _tempExercise.value.videoLinks = videoLinks

                    Toast.makeText(tilVideolink.context.applicationContext,"Video succesfully added!",Toast.LENGTH_SHORT).show()
                }else{
                    tilVideolink.error = "Error! We couldn't get de video info :'("
                }
            }
        }



    }

    fun onLongClickExerciseVideo(view: View,url:String) : Boolean{
        val applicationContext = view.context
        val builder = AlertDialog.Builder(applicationContext)
            with(builder)
            {
                setTitle("Manage Video")
                setNegativeButton("Delete",DialogInterface.OnClickListener { dialogInterface, i ->
                    updateVideoLinks(url)
                })
                setPositiveButton("Cancel", null)
                show()
            }

        return true
    }

    fun editExercise(exercise: Exercise){
        _tempExercise.value = exercise
        getVideoData()
    }
    fun updateMuscles(muscles: List<String>) {
        _tempExercise.value.muscles = muscles
    }

    fun updateVideoLinks(deletedVideo: String) {

        var newVideoData:List<Video> = _videoList.value.filter {
            it.url != deletedVideo
        }
        _videoList.value = newVideoData

        var newVideoList: List<VideoLink> = tempExercise.value.videoLinks.filter {
            it.videoUrl != deletedVideo
        }
        _tempExercise.value.videoLinks = newVideoList
    }

    fun saveStateHandle() {
        savedStateHandle.set(::tempExercise.name, tempExercise.value)
    }


}