package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.databinding.ExerciseItemBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel
import kotlin.math.absoluteValue

class ExercisesRecyclerViewAdapter(
    private var exercisesArrayList: ArrayList<Exercise>,
    private val mainSharedViewModel: MainSharedViewModel
): RecyclerView.Adapter<ExercisesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ExerciseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(exercisesArrayList[position])
        holder.binding.executePendingBindings()
        holder.binding.root.setOnClickListener {
            mainSharedViewModel.setSelectedExercise(it,exercisesArrayList[position])
        }
    }

    override fun getItemCount() = exercisesArrayList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(bookings: ArrayList<Exercise>) {
        exercisesArrayList = bookings
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: ExerciseItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(exercise: Exercise) {
            binding.exercise = exercise
            binding.sModel = mainSharedViewModel
        }
    }
}