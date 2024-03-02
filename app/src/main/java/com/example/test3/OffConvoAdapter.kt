package com.example.test3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OffConvoAdapter(val storedConvo: ArrayList<Convo>): RecyclerView.Adapter<OffConvoAdapter.OffConvoHolder>() {
    private lateinit var poslistener: ItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OffConvoHolder{
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return OffConvoHolder(view, poslistener)
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        poslistener = listener
    }

    override fun getItemCount(): Int {
        return storedConvo.size
    }

    override fun onBindViewHolder(holder: OffConvoHolder, position: Int) {
        val currentConvo = storedConvo[position]

        holder.Convoid.text = currentConvo.cid.toString()
        holder.ConvoName.text = currentConvo.deviceName
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

    //var onItemClick : ((Convo) -> Unit)? = null

//    class OffConvoHolder(itemView: View, listener: ItemClickListener) : RecyclerView.ViewHolder(itemView){

        class OffConvoHolder(itemView: View, listener: ItemClickListener) : RecyclerView.ViewHolder(itemView){
        val ConvoName= itemView.findViewById<TextView>(R.id.ConvoName)
        val Convoid = itemView.findViewById<TextView>(R.id.Cid)
        val ConvoPic = itemView.findViewById<ImageView>(R.id.Convopic)

        init {

            itemView.setOnClickListener{
                listener.onClickPosition(adapterPosition)
            }

        }
    }
}