package com.datatree.activitys

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.datatree.R
import com.datatree.adapters.CarouselAdapter
import kotlin.math.abs

class CultivosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cultivos)

        val images = listOf(
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo,
            R.drawable.logo
        )

        mostrarCarousel(images)
    }

    fun mostrarCarousel(images : List<Int>){
        val recycler = findViewById<RecyclerView>(R.id.recyclerViewCarousel)

        val adaptador = CarouselAdapter(images)
        recycler.adapter = adaptador

        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
                val centerX = recyclerView.width/2
                val childCount = recyclerView.childCount

                for (i in 0 until childCount){
                    val child = recyclerView.getChildAt(i)
                    val childCenterX = (child.left + child.right) / 2
                    val scale = 1- abs(centerX-childCenterX).toFloat() / recyclerView.width
                    child.scaleX = 0.8f + scale*0.2f
                    child.scaleY = 0.08f + scale*0.2f
                }
            }
        })
    }
}