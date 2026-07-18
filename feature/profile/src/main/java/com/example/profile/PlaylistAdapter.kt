package com.example.profile
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.model.UserManager
import com.example.profile.databinding.ItemPlaylistBinding
import com.example.profile.model.playlist.Playlist

class PlaylistAdapter : ListAdapter<Playlist, PlaylistAdapter.ViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Playlist) {
            binding.tvPlaylistName.text = item.name
            binding.tvPlaylistDescription.text = "歌单|${item.playCount}首|${UserManager.profile.value?.nickname ?: "null"}"
        }
    }
}
class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>(){
    override fun areItemsTheSame(
        p0: Playlist,
        p1: Playlist
    ): Boolean {
        return p0.id==p1.id
    }

    override fun areContentsTheSame(
        p0: Playlist,
        p1: Playlist
    ): Boolean {
        return p0==p1
    }
}