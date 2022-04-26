package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.databinding.TrainingExerciseItemBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.RecordLog
import es.ucm.fdi.workitout.model.Routine
import es.ucm.fdi.workitout.view.TrainingExerciseFragment
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel

class TrainingRecordsRecyclerViewAdapter(
    private var recordLogs: List<RecordLog>,
    private var exercise: Exercise,
    private var routine: Routine,
    private var colorCardDefault: Int,
    private var colorCardLogged: Int,
    private var colorTextDefault: Int,
    private var colorTextLogged: Int,
    private val mainSharedViewModel: MainSharedViewModel,
    private val trainingExerciseFragment: TrainingExerciseFragment
): RecyclerView.Adapter<TrainingRecordsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TrainingExerciseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recordLogs[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = recordLogs.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(recordLogs: List<RecordLog>, routine: Routine) {
        this.recordLogs = recordLogs
        this.exercise = routine.exercises.firstOrNull { it.id == this.exercise.id } ?: Exercise()
        this.routine = routine
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: TrainingExerciseItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recordLog: RecordLog) {
            binding.recordLog = recordLog
            binding.routine = routine
            binding.exercise = exercise
            binding.colorCardDefault = colorCardDefault
            binding.colorCardLogged = colorCardLogged
            binding.colorTextDefault = colorTextDefault
            binding.colorTextLogged = colorTextLogged
            binding.fragment = trainingExerciseFragment
        }
    }
}

