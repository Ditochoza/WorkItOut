package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import es.ucm.fdi.workitout.databinding.FragmentLoginBinding
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.viewModel.StartSharedViewModel

class LoginFragment : Fragment() {
    private val startSharedViewModel: StartSharedViewModel by activityViewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.sModel = startSharedViewModel
        binding.loading = startSharedViewModel.loading.value
        binding.lifecycleOwner = viewLifecycleOwner

        setupCollectors()

        return binding.root
    }

    private fun setupCollectors() {
        startSharedViewModel.loading.collectLatestFlow(this) { binding.loading = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}