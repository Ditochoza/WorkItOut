package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentHomeBinding
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class HomeFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupCollectors()

        return binding.root
    }

    private fun setupCollectors() {
        mainSharedViewModel.user.collectLatestFlow(this) { user ->
            if (user.name.isNotEmpty()) {
                binding.tvWelcome.text = getString(R.string.welcome_user_name, user.name)
                binding.pBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}