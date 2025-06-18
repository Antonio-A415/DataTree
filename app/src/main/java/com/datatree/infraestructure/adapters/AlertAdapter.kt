package com.datatree.infraestructure.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.datatree.R
import com.datatree.infraestructure.dataclass.AlertPost
import org.json.JSONArray

class AlertAdapter(

    private val context: Context,
    private val alertList: List<AlertPost>
) : RecyclerView.Adapter<AlertAdapter.AlertViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post_alert_feed_fragment, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alertList[position]
        holder.title.text = alert.title
        holder.description.text = alert.description
        holder.region.text = alert.region

        val imageUrls = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(alert.images)
            for (i in 0 until jsonArray.length()) {
                imageUrls.add(jsonArray.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val imageAdapter = ImageViewPagerAdapter_feed_fragment(context, imageUrls)
        holder.viewPager.adapter = imageAdapter


    }

    override fun getItemCount(): Int = alertList.size

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txt_title)
        val description: TextView = itemView.findViewById(R.id.txt_description)
        val region: TextView = itemView.findViewById(R.id.txt_region)
        val viewPager: ViewPager2 = itemView.findViewById(R.id.image_viewpager)
    }

}