package com.datatree.infraestructure.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.datatree.R
import com.datatree.databinding.ItemMainRecyclerViewBinding

class MainRecyclerViewAdapter(private val listener: Listener):  RecyclerView.Adapter<MainRecyclerViewAdapter.MainRecyclerViewHolder>() {

    private var userList : List<Pair<String, String>> ? =null

    fun updateList(list: List<Pair<String, String>>):Unit{
        this.userList=list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainRecyclerViewHolder {
        val binding = ItemMainRecyclerViewBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return MainRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList?.size?:0
    }

    interface  Listener {
        fun onVideoCallClicked(username:String)
        fun onAudioCallClicked(username:String)
    }


    override fun onBindViewHolder(holder: MainRecyclerViewHolder, position: Int) {
        userList?.let { list->
            val user = list[position]
            holder.bind(user,{
                listener.onVideoCallClicked(it)
            },{
                listener.onAudioCallClicked(it)
            })
        }
    }




    //Definir la clase interna

    class MainRecyclerViewHolder(private val binding: ItemMainRecyclerViewBinding): RecyclerView.ViewHolder(binding.root){
        private val context = binding.root.context

        //Con esta funcion pasamos dos parametros, el nombre del user y su estado (ONLINE, OFFLINE, IN_CALL)
        //ademas enviamos dos parametros llamados videoCallClicked y audioCallClicked ambos son expressiones lambda.
        fun bind(user : Pair<String,String>, videoCallClicked: (String)->Unit, audioCallClicked :(String) -> Unit){
            binding.apply {
                //Similar al uso del switch en otros lenguajes

                when (user.second) {
                    //Cuando esta en linea
                    "ONLINE" -> {
                        videoCallBtn.isVisible = true
                        audioCallBtn.isVisible = true
                        videoCallBtn.setOnClickListener {
                            videoCallClicked.invoke(user.first)
                        }
                        audioCallBtn.setOnClickListener {
                            audioCallClicked.invoke(user.first)
                        }
                        statusTv.setTextColor(context.resources.getColor(R.color.green_strong, null))
                        statusTv.text = "Online"
                    }
                    //Cuando esta fuera de linea
                    "OFFLINE" -> {
                        videoCallBtn.isVisible = false
                        audioCallBtn.isVisible = false
                        statusTv.setTextColor(context.resources.getColor(R.color.white, null))
                        statusTv.text = "Offline"
                    }
                    //Cuando el user esta en llamada
                    "IN_CALL" -> {
                        videoCallBtn.isVisible = false
                        audioCallBtn.isVisible = false
                        statusTv.setTextColor(context.resources.getColor(R.color.green_more_strong, null))
                        statusTv.text = "In Call"
                    }
                }

                usernameTv.text = user.first
            }
        }


    }

}