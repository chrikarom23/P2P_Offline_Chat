package com.example.test3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class OffMessageAdapter(context: Context, val storedConvo: ArrayList<message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var poslistener: ItemClickListener
    companion object {
        private const val TYPE_SENDER = 0
        private const val TYPE_RECIEVER = 1
    }

    private val mycontext: Context = context
    var sC: ArrayList<message> = storedConvo

    private inner class SenderView(itemView: View): RecyclerView.ViewHolder(itemView){
        var TextLine = itemView.findViewById<TextView>(R.id.smessage)
        var Timestampp = itemView.findViewById<TextView>(R.id.stimestamp)
        fun bind(position: Int){
            val Sdata = sC[position]
            TextLine.text = Sdata.line_text
            Timestampp.text = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.UK).format(Date(Sdata.timestampp))
        }
    }

    private inner class RecieverView(itemView: View): RecyclerView.ViewHolder(itemView){
        var TextLine = itemView.findViewById<TextView>(R.id.Rmessage)
        var Timestampp = itemView.findViewById<TextView>(R.id.Rtimestamp)
        var uname = itemView.findViewById<TextView>(R.id.user)
        fun bind(position: Int){
            val Sdata = sC[position]
            TextLine.text = Sdata.line_text
            Timestampp.text = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.UK).format(Date(Sdata.timestampp))
            uname.text = Sdata.uname
        }
    }

    override fun getItemViewType(position: Int): Int {
        var uname = sC[position].uname
        if (uname == "You") {
            return TYPE_SENDER
        }
        else{
            return TYPE_RECIEVER
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder{
        if(viewType == TYPE_SENDER){
            return SenderView(LayoutInflater.from(mycontext).inflate(R.layout.recylcer_view_sender,parent,false))
        }
        else{
            return RecieverView(LayoutInflater.from(mycontext).inflate(R.layout.recycler_view_reciever,parent,false))
        }
    }

    fun setOnItemClickListener(listener: ItemClickListener){
        poslistener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(sC[position].uname == "You"){
            (holder as SenderView).bind(position)
        }
        else{
            (holder as RecieverView).bind(position)
        }
    }

    override fun getItemCount(): Int {
        return storedConvo.size
    }

//    class OffMessageViewHolder(itemView: View, listener: ItemClickListener) : RecyclerView.ViewHolder(itemView){
//        val TextLine = itemView.findViewById<TextView>(R.id.ConvoName)
//        val Timestampp = itemView.findViewById<TextView>(R.id.Gid)
//        init {
//
//            itemView.setOnClickListener{
//                listener.onClickPosition(adapterPosition)
//            }
//
//        }
//    }

}