package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.databinding.VideoItemBinding
import es.ucm.fdi.workitout.model.Exercise
import es.ucm.fdi.workitout.model.Video
import es.ucm.fdi.workitout.view.ViewExerciseFragment
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class VideosRecyclerViewAdapter(
    private var videos: List<Video>,
    private var exercise: Exercise,
    private val mainSharedViewModel: MainSharedViewModel,
    private val viewExerciseFragment: ViewExerciseFragment
): RecyclerView.Adapter<VideosRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videos[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = videos.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(videos: List<Video>) {
        this.videos = videos
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: VideoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video) {
            binding.video = video
            binding.exercise = exercise
            binding.fragment = viewExerciseFragment
            binding.sModel = mainSharedViewModel
        }
    }
}