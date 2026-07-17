import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.search.R
import com.example.search.model.SongItem

class SearchResultAdapter(
    private val dataList: MutableList<SongItem> = mutableListOf()
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSongName: TextView = itemView.findViewById(R.id.tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_search,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = dataList[position]

        holder.tvSongName.text = song.name

    }

    fun setData(newList: List<SongItem>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged() // 刷新列表
    }
}