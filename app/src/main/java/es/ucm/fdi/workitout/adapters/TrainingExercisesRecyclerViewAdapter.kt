package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.databinding.SelectExerciseItemBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class TrainingExercisesRecyclerViewAdapter(
    private var exercises: List<Exercise>,
    private var selectedRoutine: Routine,
    private val mainSharedViewModel: MainSharedViewModel
): RecyclerView.Adapter<TrainingExercisesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SelectExerciseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(exercises[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = exercises.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(exercises: List<Exercise>) {
        this.exercises = exercises
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: SelectExerciseItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: Exercise) {
            binding.exercise = exercise
            binding.sModel = mainSharedViewModel
        }
    }
}

