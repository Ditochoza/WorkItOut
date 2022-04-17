package es.ucm.fdi.workitout.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentMyExercisesBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.utils.collectLatestFlow
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class MyExercisesFragment : Fragment() {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var _binding: FragmentMyExercisesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyExercisesBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.user = mainSharedViewModel.user.value
        binding.myExercises = this

        setupCollectors()

        return binding.root
    }

    fun onLongClickExercise (view:View,exercise: Exercise):Boolean {
        //val applicationContext = this.activity!!.applicationContext
        val applicationContext = view.context
        val items = arrayOf("Edit","Delete")
        val builder = AlertDialog.Builder(applicationContext)

        with(builder)
        {
            setTitle("Manage Exercise")
            setItems(items) { dialog, which ->
                if(items[which] == "Edit"){
                    mainSharedViewModel.setSelectedExercise(exercise)
                    view.findNavController().navigate(R.id.action_myExercisesFragment_to_createExerciseFragment)
                }else if(items[which] == "Delete") {
                    mainSharedViewModel.deleteExercise(exercise)
                    Toast.makeText(applicationContext, "Exercise deleted", Toast.LENGTH_SHORT).show()
                }

            }

            setPositiveButton("Cancel", null)
            show()
        }

        return true
    }

    private fun setupCollectors() {
        mainSharedViewModel.user.collectLatestFlow(this) { binding.user = it }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


