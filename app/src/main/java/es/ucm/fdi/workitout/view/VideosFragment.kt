package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import es.ucm.fdi.workitout.databinding.FragmentCreateExerciseBinding
import es.ucm.fdi.workitout.databinding.FragmentVideosBinding
import es.ucm.fdi.workitout.model.Video
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.viewModel.CreateExerciseViewModel
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class VideosFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVideosBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
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
}


