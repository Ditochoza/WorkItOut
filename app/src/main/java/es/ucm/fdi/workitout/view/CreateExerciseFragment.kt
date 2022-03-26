package es.ucm.fdi.workitout.view


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import es.ucm.fdi.workitout.R
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
            checkedIds.forEach { chipId ->
                chipsText.add(binding.chipGroup.findViewById<Chip>(chipId).text.toString())
            }

            editSharedViewModel.selectedMuscles = chipsText
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}