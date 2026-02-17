package com.datatree.infraestructure.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.datatree.infraestructure.dataclass.Itemsplants
import com.datatree.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions

class CultivosAdapter(
    private var itemsplants: MutableList<Itemsplants>,
    val context: Context,
    private val onVerGuiaCompleta: (Itemsplants) -> Unit,
    private val onImageClick: (Itemsplants, Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ONE = 0  // Header/Category
        const val VIEW_TYPE_TWO = 1  // Plant item
    }

    private val _context = context
    private var _list: List<Itemsplants> = itemsplants

    // Método para actualizar la lista
    fun updateList(newList: List<Itemsplants>) {
        _list = newList
        itemsplants.clear()
        itemsplants.addAll(newList)
        notifyDataSetChanged()
    }

    private inner class View1ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.title)
        var img: ImageView = itemView.findViewById(R.id.imgilusId)

        fun bind(position: Int) {
            val recyclerViewModel = _list[position]
            message.text = recyclerViewModel.nombre
            img.setImageResource(recyclerViewModel.img)
        }
    }

    private inner class View2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nme: TextView = itemView.findViewById(R.id.namId)
        var nme2: TextView = itemView.findViewById(R.id.nam2Id)
        var img: ImageView = itemView.findViewById(R.id.imgId)
        var btnVerGuia: Button = itemView.findViewById(R.id.verMasButton)

        fun bind(position: Int) {
            val recyclerItems = _list[position]
            nme.text = recyclerItems.nombre
            nme2.text = recyclerItems.nombre2

            // Cargar imagen principal
            loadPlantImage(recyclerItems)

            // Manejar el click del botón
            btnVerGuia.setOnClickListener {
                onVerGuiaCompleta(recyclerItems)
            }

            // Para poder manejar el clic de la imagen
            img.setOnClickListener {
                if (recyclerItems.hasImages()) {
                    onImageClick(recyclerItems, 0) // empezar desde la primera imagen
                }
            }
        }

        private fun loadPlantImage(item: Itemsplants) {
            if (item.hasImages()) {
                val primaryImageUrl = item.getPrimaryImageUrl()

                // Usar Glide para cargar la imagen de internet con fallback al drawable
                Glide.with(_context)
                    .load(primaryImageUrl)
                    .apply(RequestOptions()
                        .placeholder(R.drawable.access_time_24) // Mostrar drawable mientras carga
                        .error(R.drawable.cloudy_24) // Mostrar drawable si falla la carga
                        .centerCrop())
                    .into(img)
            } else {
                // Si no hay URLs, usar el drawable por defecto
                img.setImageResource(R.drawable.icon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ONE) {
            View1ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.itemtitletypeplants, parent, false)
            )
        } else {
            View2ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.itemplants, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return _list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (_list[position].viewtype == VIEW_TYPE_ONE) {
            (holder as View1ViewHolder).bind(position)
        } else {
            (holder as View2ViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return _list[position].viewtype
    }

    // Métodos adicionales de utilidad
    fun addItem(item: Itemsplants) {
        itemsplants.add(item)
        _list = itemsplants
        notifyItemInserted(itemsplants.size - 1)
    }

    fun removeItem(position: Int) {
        if (position >= 0 && position < itemsplants.size) {
            itemsplants.removeAt(position)
            _list = itemsplants
            notifyItemRemoved(position)
        }
    }

    fun clearItems() {
        val size = itemsplants.size
        itemsplants.clear()
        _list = itemsplants
        notifyItemRangeRemoved(0, size)
    }

    fun getItem(position: Int): Itemsplants? {
        return if (position >= 0 && position < _list.size) {
            _list[position]
        } else null
    }

    fun findItemById(plantId: Long): Itemsplants? {
        return _list.find { it.plantId == plantId }
    }
}