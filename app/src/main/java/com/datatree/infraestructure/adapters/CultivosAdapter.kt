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

class CultivosAdapter(
    private var Itemsplants: MutableList<Itemsplants>,
    val context: Context,
    private val onVerGuiaCompleta: (Itemsplants) ->Unit //llamar un callback
):RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    companion object {
        const val VIEW_TYPE_ONE = 0
        const val VIEW_TYPE_TWO = 1
    }

    private val _context=context
    var _list :List<Itemsplants> =Itemsplants


    private inner class View1ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var message: TextView = itemView.findViewById(R.id.title)
        var img : ImageView = itemView.findViewById(R.id.imgilusId)
        fun bind(position: Int) {
            val recyclerViewModel = _list[position]
            message.text = recyclerViewModel.nombre
            img.setImageResource(recyclerViewModel.img)
        }
    }
    private inner class View2ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var nme: TextView = itemView.findViewById(R.id.namId)

        var nme2 : TextView = itemView.findViewById(R.id.nam2Id)

        var img : ImageView = itemView.findViewById(R.id.imgId)

        var btnVerGuia: Button = itemView.findViewById(R.id.verMasButton)

        fun bind(position: Int) {
            val recyclerItems = _list[position]
            nme.text = recyclerItems.nombre
            nme2.text= recyclerItems.nombre2
            img.setImageResource(recyclerItems.img)

            //manejar el click del boton
            btnVerGuia.setOnClickListener{
                onVerGuiaCompleta(recyclerItems)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ONE) {
            return View1ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.itemtitletypeplants, parent, false)
            )
        }
        return View2ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.itemplants, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return _list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (_list[position].viewtype === VIEW_TYPE_ONE) {
            (holder as View1ViewHolder).bind(position)
        } else {
            (holder as View2ViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return _list[position].viewtype
    }
}