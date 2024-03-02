package com.example.test3

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class Setts:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setTitle("Settings")


        //val wifiBtn = findViewById<Button>(R.id.button)
        //val InternetBtn = findViewById<Button>(R.id.button2)
        //val IPFSBtn = findViewById<Button>(R.id.button3)
        val DataBtn = findViewById<Button>(R.id.button4)
        //val AboutBtn = findViewById<Button>(R.id.button5)

        DataBtn.setOnClickListener{
            val dao = Chat_Database.getInstance(this).chatDao
            val builder = AlertDialog.Builder(this@Setts)
            builder.setTitle("Chat Deletion Confirmation")
            builder.setMessage("Are you sure of your actions? The data being purged is irrecoverable.")
            builder.setIcon(R.drawable.baseline_warning_24)
            builder.setPositiveButton("YES"){
                dialog,id ->
                cleardata()
            }
            builder.setNegativeButton("CANCEL"){
                dialog,id ->
                Toast.makeText(this, "Deletion Canceled", Toast.LENGTH_LONG).show()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

    }

    fun cleardata(){
        val dao = Chat_Database.getInstance(this).chatDao
        lifecycleScope.launch {
            Log.i("Settings",  "Deleting all data")

            dao.deletechats()
            dao.deletechatdata()
        }
    }
}