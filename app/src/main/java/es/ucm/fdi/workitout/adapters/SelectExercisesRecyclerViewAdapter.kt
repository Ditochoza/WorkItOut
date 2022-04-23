package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.databinding.SelectExerciseItemBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.view.SelectExercisesFragment
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class SelectExercisesRecyclerViewAdapter(
    private var exercisesList: List<Exercise>,
    private var selectedRoutine: Routine,
    private val mainSharedViewModel: MainSharedViewModel,
    private val selectExercisesFragment: SelectExercisesFragment,
    private val colorDefault: Int,
    private val colorSelected: Int
): RecyclerView.Adapter<SelectExercisesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SelectExerciseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(exercisesList[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = exercisesList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(exercises: List<Exercise>, routine: Routine) {
        exercisesList = exercises
        selectedRoutine = routine
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: SelectExerciseItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: Exercise) {
            binding.exercise = exercise
            binding.routine = selectedRoutine
            binding.selected = selectedRoutine.exercises.map { it.id }.contains(exercise.id)
            binding.fragment = selectExercisesFragment
            binding.sModel = mainSharedViewModel
            binding.colorDefault = colorDefault
            binding.colorSelected = colorSelected
            binding.muscles = exercise.muscles.joinToString(", ")
        }
    }
}

