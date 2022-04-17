package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentAddVideoLinksBinding
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.viewModel.CreateExerciseViewModel
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class AddVideoLinksFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()
    private val viewModel: CreateExerciseViewModel by activityViewModels()

    private var _binding: FragmentAddVideoLinksBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddVideoLinksBinding.inflate(inflater, container, false)


        binding.sModel = mainSharedViewModel
        binding.vModel = viewModel
        binding.mainActivity = activity as MainActivity?
        binding.lifecycleOwner = viewLifecycleOwner


        setupCollectors()

        return binding.root
    }

    private fun setupCollectors() {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Guardamos el estado del ViewModel cuando la app pasa a segundo plano
    override fun onPause() {
        viewModel.saveStateHandle()
        super.onPause()
    }
}


