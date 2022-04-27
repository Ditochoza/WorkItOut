package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.databinding.RecordItemBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Record


class RecordsRecyclerViewAdapter(
    private var records: List<Record>,
    private var exercise: Exercise,
): RecyclerView.Adapter<RecordsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecordItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(records[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = records.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(records: List<Record>) {
        this.records = records
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: RecordItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(record: Record) {
            binding.record = record
            binding.exercise = exercise
        }
    }
}