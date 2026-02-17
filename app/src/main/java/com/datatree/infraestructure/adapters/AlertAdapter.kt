package com.datatree.infraestructure.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.datatree.R
import com.datatree.infraestructure.dataclass.AlertPost
import com.google.android.material.tabs.TabLayout
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

        holder.tabLayout.removeAllTabs()
        holder.tabLayout.addTab(holder.tabLayout.newTab().setText("Métricas"))
        holder.tabLayout.addTab(holder.tabLayout.newTab().setText("Geoinformación"))

        inflateMetricas(holder.container, alert)
        //escuchar cambio de tab
        
        holder.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> inflateMetricas(holder.container, alert)
                    1 -> inflateGeoinformacion(holder.container, alert)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })



    }

    override fun getItemCount(): Int = alertList.size

    private fun inflateMetricas(container: FrameLayout, alert: AlertPost){
        container.removeAllViews()
        val view = LayoutInflater.from(context).inflate(R.layout.view_metrics, null)
        //el metodo para inflar recibe dos parametros el view y su contenedor
        //view.findViewById<

        // Quitar de su padre si lo tiene
        (view.parent as? ViewGroup)?.removeView(view)

        // Limpiar el contenedor y agregar la nueva vista
        container.removeAllViews()
        container.addView(view)
    }

    private fun inflateGeoinformacion(container: FrameLayout, alert: AlertPost){
        container.removeAllViews()
        val view = LayoutInflater.from(context).inflate(R.layout.view_geoinformacion, null)
        // Quitar de su padre si lo tiene
        (view.parent as? ViewGroup)?.removeView(view)

// Limpiar el contenedor y agregar la nueva vista
        container.removeAllViews()
        container.addView(view)
    }

    class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val tabLayout: TabLayout = itemView.findViewById<TabLayout>(R.id.image_indicator)
        val container = itemView.findViewById<FrameLayout>(R.id.container_alerts_item)



        val title: TextView = itemView.findViewById(R.id.txt_title)
        val description: TextView = itemView.findViewById(R.id.txt_description)
        val region: TextView = itemView.findViewById(R.id.txt_region)
        val viewPager: ViewPager2 = itemView.findViewById(R.id.image_viewpager)

    }

}