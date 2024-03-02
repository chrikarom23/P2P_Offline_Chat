package com.example.test3

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class messaging : AppCompatActivity(){

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var sendMessageEditText: EditText
    private lateinit var sendMessageButton: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Log.d("MessagingView", "In messaging Activity")
        setContentView(R.layout.activity_chatview)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        //val bundle = arguments
        //val message = bundle!!.getString("")


        messageRecyclerView = findViewById(R.id.messageRecyclerView)
        sendMessageButton = findViewById(R.id.sendMessage)
        sendMessageEditText = findViewById(R.id.sendMessageEditText)
    }
}