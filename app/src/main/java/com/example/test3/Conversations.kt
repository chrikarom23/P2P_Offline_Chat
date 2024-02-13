package com.example.test3

import android.content.AsyncQueryHandler
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import com.example.test3.ConvoDatabase as ConvoDatabase  


class Conversations: AppCompatActivity() {
    lateinit var OffConvoRecyclerView: RecyclerView
    private lateinit var storedConvo: ArrayList<Convo>
    private lateinit var OCadapter: OffConvoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convos)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        Dbaccess()
        //init()

    }

    fun Dbaccess(){
        val db = Room.databaseBuilder(applicationContext,ConvoDatabase  ::class.java,"ConversationDB").allowMainThreadQueries().build()
        val testconvo: ConvoData = ConvoData(1, "Tester", "12:12:12")
        db.convodao.upsertConvo(testconvo)
        var convos = db.convodao.getConvosByTimestamp()
        Log.d("Conversations","Convos added: ${convos.toString()}")
        storedConvo = ArrayList<Convo>()
        storedConvo.add(Convo(convos[0].id.toString(),convos[0].deviceName,true))
        storedConvo.add(Convo(convos[0].id.toString(),"Thar",true))
        storedConvo.add(Convo(convos[0].id.toString(),"Kuri",true))
        OCadapter = OffConvoAdapter(storedConvo)
        OffConvoRecyclerView = findViewById<RecyclerView>(R.id.OffConvoRecyclerView)
        OffConvoRecyclerView.layoutManager = LinearLayoutManager(this)
        OffConvoRecyclerView.adapter = OCadapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_s,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_hmmm -> {
            // User chooses the "Settings" item. Show the app settings UI.
            Log.i("Mine", "Hmmmmmm")
            true
        }

        R.id.action_settings -> {
            // User chooses the "Favorite" action. Mark the current item as a
            // favorite.
            Log.i("Mine", "Settings action Working")
            val intent1 = Intent(this, Setts::class.java)
            startActivity(intent1)
            true
        }

        else -> {
            // The user's action isn't recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}