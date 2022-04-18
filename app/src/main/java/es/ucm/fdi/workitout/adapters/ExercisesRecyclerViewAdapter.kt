package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.databinding.ExerciseItemBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.view.ExercisesFragment
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class ExercisesRecyclerViewAdapter(
    private var exercisesList: List<Exercise>,
    private val mainSharedViewModel: MainSharedViewModel,
    private val exercisesFragment: ExercisesFragment
): RecyclerView.Adapter<ExercisesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ExerciseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(exercisesList[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = exercisesList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(exercises: List<Exercise>) {
        exercisesList = exercises
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: ExerciseItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: Exercise) {
            binding.exercise = exercise
            binding.fragment = exercisesFragment
            binding.sModel = mainSharedViewModel
        }
    }
}

