package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.databinding.ExerciseItemBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.view.MainActivity
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class ExercisesRecyclerViewAdapter(
    private var exercises: List<Exercise>,
    private val mainSharedViewModel: MainSharedViewModel,
    private val mainActivity: MainActivity,
    private val navActionResToEdit: Int,
    private val navActionResToView: Int
): RecyclerView.Adapter<ExercisesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ExerciseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
    inner class ViewHolder(val binding: ExerciseItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: Exercise) {
            binding.exercise = exercise
            binding.activity = mainActivity
            binding.sModel = mainSharedViewModel
            binding.navActionResToEdit = navActionResToEdit
            binding.navActionResToView = navActionResToView
            binding.emptyExercise = Exercise()
            binding.muscles = exercise.muscles.joinToString(", ")
        }
    }
}

