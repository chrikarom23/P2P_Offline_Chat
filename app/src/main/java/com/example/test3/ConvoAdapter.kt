package com.example.test3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ConvoAdapter(val availableConvo: ArrayList<Convo>): RecyclerView.Adapter<ConvoAdapter.ConvoViewHolder>(){
    private lateinit var poslistener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConvoViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ConvoViewHolder(view, poslistener)
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        poslistener = listener
    }
    override fun onBindViewHolder(holder: ConvoViewHolder, position: Int) {
        val currentConvo = availableConvo[position]

        holder.ConvoName.text = currentConvo.id
        holder.Convoid.text = currentConvo.deviceName
        if(currentConvo.privacy==true){
            holder.ConvoPic.setImageResource(R.drawable.spy)
        }
        else{
            holder.ConvoPic.setImageResource(R.drawable.person)
        }
//        holder.itemView.setOnClickListener{
//            onItemClick?.invoke(currentConvo)
//        }

    }

    override fun getItemCount(): Int {
       return availableConvo.size
    }


    //var onItemClick : ((Convo) -> Unit)? = null


    class ConvoViewHolder(itemView: View, listener: ItemClickListener) : RecyclerView.ViewHolder(itemView){
        val ConvoName = itemView.findViewById<TextView>(R.id.ConvoName)
        val Convoid = itemView.findViewById<TextView>(R.id.Gid)
        val ConvoPic = itemView.findViewById<ImageView>(R.id.Convopic)

        init {

            itemView.setOnClickListener{
                listener.onClickPosition(adapterPosition)
            }

        }

    }
}