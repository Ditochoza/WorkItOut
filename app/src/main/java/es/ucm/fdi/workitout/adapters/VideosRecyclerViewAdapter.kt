package es.ucm.fdi.workitout.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.ucm.fdi.workitout.databinding.VideoItemBinding
import es.ucm.fdi.workitout.model.Video
import es.ucm.fdi.workitout.viewModel.MainSharedViewModel


class VideosRecyclerViewAdapter(
    private var videosArrayList: ArrayList<Video>,
    private val mainSharedViewModel: MainSharedViewModel,
): RecyclerView.Adapter<VideosRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VideoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videosArrayList[position])
        holder.binding.executePendingBindings()
        holder.binding.root.setOnClickListener {
            mainSharedViewModel.goToVideoLink(it,videosArrayList[position].url)
        }
    }

    override fun getItemCount() = videosArrayList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(bookings: ArrayList<Video>) {
        videosArrayList = bookings
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: VideoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video) {
            binding.video = video
            binding.sModel = mainSharedViewModel
        }
    }
}
