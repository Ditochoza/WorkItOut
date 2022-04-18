package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.databinding.RoutineItemBinding
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class RoutinesRecyclerViewAdapter(
    private var routinesArrayList: ArrayList<Routine>,
    private val mainSharedViewModel: MainSharedViewModel
): RecyclerView.Adapter<RoutinesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RoutineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(routinesArrayList[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = routinesArrayList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(routines: ArrayList<Routine>) {
        routinesArrayList = routines
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: RoutineItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(routine: Routine) {
            binding.routine = routine
            binding.sModel = mainSharedViewModel
        }
    }
}