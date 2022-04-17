package es.ucm.fdi.workitout.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.databinding.FragmentMyRoutinesBinding
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class MyRoutinesFragment : Fragment() {

    private val mainSharedViewModel: MainSharedViewModel by activityViewModels()

    private var _binding: FragmentMyRoutinesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyRoutinesBinding.inflate(inflater, container, false)

        binding.sModel = mainSharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.myRoutines = this

        return binding.root
    }

    fun onLongClickRoutine (view: View, routine: Routine):Boolean {
        //val applicationContext = this.activity!!.applicationContext
        val applicationContext = view.context
        val items = arrayOf("Edit","Delete")
        val builder = AlertDialog.Builder(applicationContext)

        with(builder)
        {
            setTitle("Manage Routine")
            setItems(items) { dialog, which ->
                if(items[which] == "Edit"){
                    mainSharedViewModel.setSelectedRoutine(routine)
                    view.findNavController().navigate(R.id.action_myRoutinesFragment_to_createRoutineFragment)
                }else if(items[which] == "Delete") {
                    mainSharedViewModel.deleteRoutine(routine)
                    Toast.makeText(applicationContext, "Routine deleted", Toast.LENGTH_SHORT).show()
                }

            }

            setPositiveButton("Cancel", null)
            show()
        }

        return true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}