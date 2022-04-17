package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.view.MyExercisesFragment
import es.ucm.fdi.workitout.view.MyRoutinesFragment
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class MyRoutinesRecyclerViewAdapter(
    private var routinesArrayList: ArrayList<Routine>,
    private val mainSharedViewModel: MainSharedViewModel,
    private val myRoutinesFragment: MyRoutinesFragment
): RecyclerView.Adapter<MyRoutinesRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RoutineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(routinesArrayList[position])
        holder.binding.executePendingBindings()
        holder.binding.root.setOnClickListener {
            mainSharedViewModel.setSelectedRoutine(it,routinesArrayList[position])
        }
        holder.binding.root.setOnLongClickListener {
            myRoutinesFragment.onLongClickRoutine(it,routinesArrayList[position])
        }
    }

    override fun getItemCount() = routinesArrayList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(bookings: ArrayList<Routine>) {
        routinesArrayList = bookings
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: RoutineItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(routine: Routine) {
            binding.routine = routine
            binding.sModel = mainSharedViewModel
        }
    }
}