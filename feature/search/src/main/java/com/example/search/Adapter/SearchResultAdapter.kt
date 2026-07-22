import android.R.attr.radius
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.search.R
import com.example.search.model.SongItem

class SearchResultAdapter(
    private val onSongClick: (SongItem) -> Unit, // 点击整首歌的回调（用于播放）
    private val onMoreClick: (SongItem) -> Unit
) : ListAdapter<SongItem, SearchResultAdapter.ViewHolder>(SongDiffCallback()) {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.ivSearchCover)
        val tvSongName: TextView = itemView.findViewById(R.id.tvSearchSongName)
        val tvSinger: TextView = itemView.findViewById(R.id.tvSearchSinger)
        val ivMore: ImageView = itemView.findViewById(R.id.ivSearchMore)

        init {
            ivCover.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSongClick(getItem(position))
                }
            }

            ivMore.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMoreClick(getItem(position))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_search,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = getItem(position)
        holder.tvSongName.text = song.name
        holder.tvSinger.text = song.ar?.get(0)?.name?:"未知歌手"

        Log.d("hyj", "准备加载的图片地址: ${song.al}?.picUrl")
        Glide.with(holder.itemView.context)
            .load(song.al?.picUrl)
            .transform(RoundedCorners(radius))
            .into(holder.ivCover)
    }
    class SongDiffCallback : DiffUtil.ItemCallback<SongItem>() {

        override fun areItemsTheSame(oldItem: SongItem, newItem: SongItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SongItem, newItem: SongItem): Boolean {
            return oldItem == newItem
        }
    }
}