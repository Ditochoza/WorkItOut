package es.ucm.fdi.workitout.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentMyExercisesBinding
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class MyExercises : Fragment() {

    private val mainSharedViewModel: MainSharedViewModel by viewModels()

    private var _binding: FragmentMyExercisesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyExercisesBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}