package com.datatree.infraestructure.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.datatree.R
import java.lang.reflect.Type

class CarouselAdapter (private val imagenes: List<Int> ): RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>(){



    class CarouselViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imageView : ImageView = view.findViewById(R.id.itemImage)
    }

    /*
    class CarouselViewHolder(view: View): RecyclerView.ViewHolder(view){
        val imageView : ImageView = view.findViewById(R.id.itemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
      val view = LayoutInflater.from(parent.context)
          .inflate(R.layout.item_image,parent, false)

        return CarouselViewHolder(view)
    }
    */

    override fun  onCreateViewHolder(parent : ViewGroup, viewType: Int) : CarouselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return CarouselViewHolder(view)
    }

    override fun getItemCount(): Int {
    return imagenes.size
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
      holder.imageView.setImageResource(imagenes[position])
    }

}