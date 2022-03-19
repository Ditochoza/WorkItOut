package es.ucm.fdi.workitout.view

import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}