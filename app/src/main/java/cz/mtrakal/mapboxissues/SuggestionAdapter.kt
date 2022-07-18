package cz.mtrakal.mapboxissues

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.search.result.SearchSuggestion

class SuggestionAdapter(val items: List<SearchSuggestion>) : RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.findViewById<TextView>(R.id.text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.item_suggestion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = items[position].fullAddress
    }

    override fun getItemCount(): Int = items.size
}
