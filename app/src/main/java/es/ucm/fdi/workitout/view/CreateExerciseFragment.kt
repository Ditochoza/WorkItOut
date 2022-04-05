package es.ucm.fdi.workitout.view


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.google.firebase.ktx.Firebase
import es.ucm.fdi.workitout.databinding.FragmentCreateExerciseBinding
import es.ucm.fdi.workitout.viewModel.EditSharedViewModel


class CreateExerciseFragment : Fragment() {
    private val editSharedViewModel: EditSharedViewModel by activityViewModels()

    private var _binding: FragmentCreateExerciseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCreateExerciseBinding.inflate(inflater, container, false)

        binding.eModel = editSharedViewModel
        binding.exerciseFrag = this
        binding.lifecycleOwner = viewLifecycleOwner

        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val chipsText = mutableListOf<String>()
            binding.textViewMuscleError.isVisible = false
            checkedIds.forEach { chipId ->
                chipsText.add(binding.chipGroup.findViewById<Chip>(chipId).text.toString())
            }

            editSharedViewModel.setMusclesList(chipsText)
        }

        if(editSharedViewModel.tempImageUri.value != Uri.EMPTY){
            binding.imageView.setImageURI(editSharedViewModel.tempImageUri.value)
        }


        return binding.root
    }

    //Se lanza el intent para elegir una imagen del almacenamiento interno
    fun selectImageFromGallery() = selectImageFromGalleryResultFlow.launch("image/*")

    //Al volver del intent se recupera el uri de la imagen elegida
    private val selectImageFromGalleryResultFlow = registerForActivityResult(
        ActivityResultContracts.GetContent()) { uri: Uri? ->

        uri?.let {
            editSharedViewModel.setTempImage(uri)
            binding.imageView.setImageURI(uri)
            binding.imageTexView.isVisible=false
            binding.imageTexViewError.visibility = View.GONE

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


