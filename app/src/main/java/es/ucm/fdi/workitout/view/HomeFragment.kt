package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.navigation.NavigationView
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentHomeBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class HomeFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.loading = mainSharedViewModel.loading.value
        binding.user = mainSharedViewModel.user.value
        binding.emptyList = emptyList<Exercise>()
        binding.lifecycleOwner = viewLifecycleOwner

        setupNavigationDrawerItemListener(binding.homeMainNavigationDrawer)
        setupCollectors()

        return binding.root
    }

    private fun setupNavigationDrawerItemListener(homeMainNavigationDrawer: NavigationView) {
        homeMainNavigationDrawer.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.exercises_nav_drawer_menu_item -> mainSharedViewModel.navigate(/*TODO Implementar navegación*/0)
                R.id.routines_nav_drawer_menu_item -> mainSharedViewModel.navigate(/*TODO Implementar navegación*/0)
            }
            true
        }
    }

    private fun setupCollectors() {
        mainSharedViewModel.loading.collectLatestFlow(this) { binding.loading = it }
        mainSharedViewModel.user.collectLatestFlow(this) { binding.user = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}