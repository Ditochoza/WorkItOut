package es.ucm.fdi.workitout.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import es.ucm.fdi.workitout.databinding.FragmentCategoriesBinding
import es.ucm.fdi.workitout.databinding.FragmentCreateExerciseBinding
import es.ucm.fdi.workitout.viewModel.EditSharedViewModel

/**
 * A fragment representing a list of Items.
 */
class CategoriesFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    private val editSharedViewModel: EditSharedViewModel by activityViewModels()
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        binding.eModel = editSharedViewModel
        binding.categorieFrag = this
        binding.lifecycleOwner = viewLifecycleOwner

        binding.list.adapter = MyCategorieRecyclerViewAdapter(editSharedViewModel.exercise.value.categories)

        //binding.list.addOnItemTouchListener()

        return binding.root
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            CategoriesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}