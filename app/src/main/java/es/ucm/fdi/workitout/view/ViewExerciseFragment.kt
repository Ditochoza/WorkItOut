package es.ucm.fdi.workitout.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentViewExerciseBinding
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel
import es.ucm.fdi.workitout.viewModel.ViewExerciseViewModel


class ViewExerciseFragment : Fragment() {
    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()
    private val viewModel: ViewExerciseViewModel by viewModels()

    private var _binding: FragmentViewExerciseBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentViewExerciseBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.exercise = mainSharedViewModel.selectedExercise.value
        binding.fragment = this
        binding.vModel = viewModel
        binding.loading = mainSharedViewModel.loading.value
        binding.lifecycleOwner = viewLifecycleOwner

        //Ocultamos el icono de editar ejercicio en caso de que el ejercicio no haya sido creado por el usuario
        binding.toolbar.menu.findItem(R.id.item_edit_menu_edit).isVisible =
            mainSharedViewModel.selectedExercise.value.idUser == mainSharedViewModel.user.value.email

        setupCollectors()

        return binding.root
    }

    private fun setupCollectors() {
        mainSharedViewModel.selectedExercise.collectLatestFlow(this) { binding.exercise = it }
        mainSharedViewModel.loading.collectLatestFlow(this) { binding.loading = it }
    }

    fun openVideo(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}