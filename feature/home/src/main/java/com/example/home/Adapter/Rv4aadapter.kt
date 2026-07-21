import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.home.Adapter.Rv3Adapte
import com.example.home.R
import com.example.home.model.Rv4Item

class RV4Adapter : ListAdapter<Rv4Item, RV4Adapter.SongViewHolder>(Rv4ItemDiffCallback()) {

    private var listener: Rv3Adapte.OnSongClickListener? = null

    //暴露一个给外部调用的设置方法
    fun OnSongClickListener4(listener: Rv3Adapte.OnSongClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item3, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val item = getItem(position)

        holder.tvSongName.text = item.song?.artists?.getOrNull(0)?.name ?: "未知歌手"

        holder.tvSingerName.text = item.name

        Glide.with(holder.itemView.context)
            .load(item.picUrl)
            .into(holder.img3)

        holder.img3.setOnClickListener {
            listener?.onSongNextPlayClick(item.id.toString(), item.name,  item.song?.artists?.getOrNull(0)?.name ?: "未知歌手")
        }

        holder.img3Play.setOnClickListener {
            listener?.onSongPlayClick(item.id.toString(), item.name,  item.song?.artists?.getOrNull(0)?.name ?: "未知歌手")
        }
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSongName: TextView = itemView.findViewById(R.id.sing)
        val tvSingerName: TextView = itemView.findViewById(R.id.singer)
        val img3: ImageView = itemView.findViewById(R.id.img3)

        val img3Play : ImageView = itemView.findViewById(R.id.img3_play)
    }

    class Rv4ItemDiffCallback : DiffUtil.ItemCallback<Rv4Item>() {

        override fun areItemsTheSame(oldItem: Rv4Item, newItem: Rv4Item): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Rv4Item, newItem: Rv4Item): Boolean {
            return oldItem == newItem
        }
    }

    interface OnSongClickListener {
        fun onSongPlayClick(id: String, songName: String, artistName: String)
        fun onSongNextPlayClick(id: String, songName: String, artistName: String)
    }
}