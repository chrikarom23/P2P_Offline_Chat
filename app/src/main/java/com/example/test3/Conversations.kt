package com.example.test3


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test3.entities.Chat
import com.example.test3.entities.Chat_line
import com.example.test3.entities.User
import kotlinx.coroutines.launch


class Conversations: AppCompatActivity() {
    lateinit var OffConvoRecyclerView: RecyclerView
    private lateinit var storedConvo: ArrayList<Convo>
    private lateinit var OCadapter: OffConvoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_convos)
        setSupportActionBar(findViewById(R.id.my_toolbar))

//        Dbaccess()
        Dbaccess()
        //init()

    }

    fun Dbaccess(){
        storedConvo = ArrayList<Convo>()
        val dao = Chat_Database.getInstance(this).chatDao
        val chatnames = listOf(
            Chat(chatname = "TestChat23", id =23),
            Chat(chatname = "KnK23", id = 24)
            )

        val chatlines = listOf(
            Chat_line(line_text = "HelloBroderr", cid = 23, uid = "0x1123123"),
            Chat_line(line_text = "Yooo broooo", cid = 24, uid = "0x5148234")
        )

        val users = listOf(
            User(uid = "0x1123123", username = "You"),
            User(uid = "0x5148234", username = "Jude")
        )
        lifecycleScope.launch {
            chatnames.forEach{dao.insertChat(it)}
            users.forEach{dao.insertUser(it)}
            chatlines.forEach{dao.insertChat_line(it)}
            val chatlist = dao.get_all_chats()
            Log.d("ConvoTest", "$chatlist")
            for(x in chatlist){
            storedConvo.add(Convo(peer = x.chatname, cid = x.id))
                //Toast.makeText(this@Conversations,test.first())
            }
            OCadapter = OffConvoAdapter(storedConvo)
            OffConvoRecyclerView = findViewById<RecyclerView>(R.id.OffConvoRecyclerView)
            OffConvoRecyclerView.layoutManager = LinearLayoutManager(this@Conversations)
            OffConvoRecyclerView.adapter = OCadapter
            OCadapter.setOnItemClickListener(object : ItemClickListener{
                override fun onClickPosition(pos: Int) {
                    Toast.makeText(this@Conversations, "clicked $pos", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Conversations, OffMessages::class.java)
                    intent.putExtra("id" , storedConvo[pos].cid)
                    startActivity(intent)
                }
            })
        }
    }

//    fun Dbaccess(){
//        val db = Room.databaseBuilder(applicationContext,ConvoDatabase::class.java,"ConversationDB").allowMainThreadQueries().build()
//        val testconvo: ConvoData = ConvoData(1, "Tester", "12:12:12")
//        db.convodao.upsertConvo(testconvo)
//        var convos = db.convodao.getConvosByTimestamp()
//        Log.d("Conversations","Convos added: ${convos.toString()}")
//        storedConvo = ArrayList<Convo>()
//        storedConvo.add(Convo(convos[0].id.toString(),convos[0].deviceName,true))
//        storedConvo.add(Convo(convos[0].id.toString(),"Thar",true))
//        storedConvo.add(Convo(convos[0].id.toString(),"Kuri",true))
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_s,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {


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

    override fun onDestroy() {
        super.onDestroy()
    }

}