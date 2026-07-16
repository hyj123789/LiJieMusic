import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.home.R
import com.example.home.model.Rv4Item

class RV4Adapter : RecyclerView.Adapter<RV4Adapter.SongViewHolder>() {

    private var dataList: List<Rv4Item> = emptyList()

    fun setData(newList: List<Rv4Item>) {
        this.dataList = newList
        notifyDataSetChanged() //刷新列表
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item3, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val item = dataList[position]

        //设置歌名
        holder.tvSongName.text = item.song?.artists?.getOrNull(0)?.name ?: "未知歌手"

        //提取歌手名字
        holder.tvSingerName.text = item.name

        Glide.with(holder.itemView.context)
            .load(item.picUrl)
            .into(holder.img3)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSongName: TextView = itemView.findViewById(R.id.sing)
        val tvSingerName: TextView = itemView.findViewById(R.id.singer)

        val img3: ImageView = itemView.findViewById(R.id.img3)
    }
}