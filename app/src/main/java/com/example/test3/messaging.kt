package com.example.test3

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class messaging: Fragment(R.layout.activity_chatview) {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var sendMessageEditText: EditText
    private lateinit var sendMessageButton: FloatingActionButton
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.activity_chatview, container, false)
        messageRecyclerView = view.findViewById(R.id.messageRecyclerView)
        sendMessageButton = view.findViewById(R.id.sendMessage)
        sendMessageEditText = view.findViewById(R.id.sendMessageEditText)
        sendMessageButton.setOnClickListener{
            sendMessage()
        }
        return view
    }

    private fun sendMessage() {
        val message = sendMessageEditText.text.toString()
        if(TextUtils.isEmpty(message)){
            sendMessageEditText.error = "Enter a message!"
        }
        else{
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minutes = c.get(Calendar.MINUTE)
            //val seconds = c.get(Calendar.SECOND)
            val timestamp = "$hour: $minutes"
            val messageObject = mutableMapOf<String, String>().also{
                //it["chatid"]
                it["message"] = message
                it["timestamp"] = timestamp
            }
        }
    }
}