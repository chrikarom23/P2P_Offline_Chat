package com.example.test3

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class OffMessages: AppCompatActivity() {

    lateinit var sC: ArrayList<message>
    lateinit var offMessageRecycler: RecyclerView
    private lateinit var offMessageAdapter: OffMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("OffMessages", "In offMessage Activity")
        setContentView(R.layout.activity_offline_chatview)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        var cid = intent.getIntExtra("id", 0)
//        supportActionBar?.setTitle(cid)
        getmessages(cid)
    }

    fun getmessages(cid: Int){
        sC = arrayListOf()
        val dao = Chat_Database.getInstance(this).chatDao
        lifecycleScope.launch {
            var getlines = dao.get_chatlines_with_cid(cid)
            var cname = dao.get_chatname(cid)
//            sC.add(message("NewTest", "Chrome", "LOREM IPSUM", timestampp = java.util.Date().time))
            for(i in getlines){
                var un = dao.get_username(i.uid)
                if(i.uid == "green"){
                    sC.add(message(i.cid.toString(),"You",i.line_text,i.timestamp))
                }
                else{
                sC.add(message(i.cid.toString(),un,i.line_text,i.timestamp))
                }
                //Log.d("OffMessages", "Printing sC: ${un}, $i")
            }
            supportActionBar?.setTitle(cname)

            offMessageAdapter = OffMessageAdapter(this@OffMessages, sC)
            offMessageRecycler = findViewById(R.id.offMessageRecyclerView)
            offMessageRecycler.layoutManager = LinearLayoutManager(this@OffMessages)
            offMessageRecycler.adapter = offMessageAdapter
        }

    }
}