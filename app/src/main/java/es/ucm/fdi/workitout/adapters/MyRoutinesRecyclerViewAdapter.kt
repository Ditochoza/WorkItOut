package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.databinding.MyRoutineItemBinding
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.view.MyRoutinesFragment
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class MyRoutinesRecyclerViewAdapter(
    private var routinesArrayList: List<Routine>,
    private val mainSharedViewModel: MainSharedViewModel,
    private val myRoutinesFragment: MyRoutinesFragment
): RecyclerView.Adapter<MyRoutinesRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MyRoutineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(routinesArrayList[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = routinesArrayList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(routines: List<Routine>) {
        routinesArrayList = routines
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: MyRoutineItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(routine: Routine) {
            binding.routine = routine
            binding.fragment = myRoutinesFragment
            binding.sModel = mainSharedViewModel
        }
    }
}