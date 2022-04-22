package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.databinding.ScheduledRoutineItemBinding
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.view.HomeFragment
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class ScheduledRoutinesRecyclerViewAdapter(
    private var routinesArrayList: ArrayList<Routine>,
    private val mainSharedViewModel: MainSharedViewModel,
    private val homeFragment: HomeFragment
): RecyclerView.Adapter<ScheduledRoutinesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ScheduledRoutineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class ViewHolder(val binding: ScheduledRoutineItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(routine: Routine) {
            binding.routine = routine
            binding.sModel = mainSharedViewModel
            binding.fragment = homeFragment
        }
    }
}