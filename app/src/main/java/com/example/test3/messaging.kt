package com.example.test3

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test3.entities.Chat
import com.example.test3.entities.Chat_line
import com.example.test3.entities.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.system.exitProcess

class messaging : AppCompatActivity(){

    lateinit var MessageRecycler: RecyclerView
    private lateinit var MessageArray: ArrayList<message>
    private lateinit var Madapter: MessageAdapter
    private lateinit var sendMessageEditText: EditText
    private lateinit var sendMessageButton: FloatingActionButton

    private lateinit var manager: WifiP2pManager
    private lateinit var channel:WifiP2pManager.Channel
    private lateinit var breceiver:BroadcastReceiver
    lateinit var intentfil: IntentFilter
    private var GOAdd: String? = "192.168.49.1"
    private lateinit var user: String
    private var EXITFLAG = 0
    //private lateinit var peer: String

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Log.d("MessagingView", "In messaging Activity")
        var igo = intent.getBooleanExtra("igo", false)
        GOAdd = intent.getStringExtra("GO")
        user = intent.getStringExtra("user") ?: "you"
        //peer = intent.getStringExtra("peer") ?: "peer"
        Toast.makeText(this, "GO Address = $GOAdd", Toast.LENGTH_LONG).show()
        setContentView(R.layout.activity_chatview)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        if(igo){
            supportActionBar?.setTitle("Pares(GO)")
        }else{
            supportActionBar?.setTitle("Pares(Cl)")
        }
         Log.d("MessagingView", "In init function")
        MessageArray = arrayListOf()

        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)
        breceiver = WifiDreciever(manager, channel, this)

        onBackPressedDispatcher.addCallback(this, object:  OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.d("Messaging", "Back pressed, Disconnecting from peers")
                disconnect()
                loopbreaker()
            }
        })

        MessageArray = arrayListOf()

        intentfil = IntentFilter()
        intentfil.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentfil.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentfil.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentfil.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        registerReceiver(breceiver, intentfil)

        MessageRecycler = findViewById(R.id.messageRecyclerView)
        sendMessageButton = findViewById(R.id.sendMessage)
        sendMessageEditText = findViewById(R.id.sendMessageEditText)

        if(igo){
            servertry()
            }
        else{
            clienttry()
        }
    }

    fun loopbreaker(){
        EXITFLAG = 1
    }

    fun servertry(){
        var sersock = ServerSocket()
        sersock.receiveBufferSize = 512
        sendMessageButton = findViewById(R.id.sendMessage)
        sersock.reuseAddress = true
        sersock.bind(InetSocketAddress(9000))
        println("server status:: ${sersock.isClosed}: ${sersock.inetAddress}")
        Thread(Runnable(){
            run {
                try{
                    Log.d("Messaging Thread", "print t4")
                    Log.d("Messaging", "in thread")
                    var ss = sersock.accept()
                    var dosS = DataOutputStream(ss.getOutputStream())
                    var dis = DataInputStream(ss.getInputStream())
                    val inputthread = Thread{
                        run{
                            try{
                                Log.d("Messaging Thread", "print t5")
                                while(true) {
                                    var tempd = ""
                                    try{
                                        while(dis.available()>0){
                                    tempd += dis.readUTF()}}
                                    catch (e: Exception){
                                        e.printStackTrace()
                                    }
                                    if(!tempd.isNullOrEmpty()){
                                    runOnUiThread(Runnable {
                                        Log.d("Messaging", "Updating message")
                                        run {
                                            var received = Gson().fromJson<message>(tempd, message::class.java)
                                            MessageArray.add(received)
                                            Madapter = MessageAdapter(this@messaging, MessageArray)
                                            MessageRecycler.layoutManager = LinearLayoutManager(this@messaging)
                                            MessageRecycler.adapter = Madapter
                                        }
                                    })}
                                    if(EXITFLAG==1){
                                        break
                                    }
                                }
                            }
                            catch (e: Exception){
                                println(e)
                            }
                        }
                    }
                    inputthread.start()
                        try{
                            while(true) {
                                //Log.d("MessagingView", "im in heree")
                                sendMessageButton.setOnClickListener{
                                    Log.d("Messaging", user)
                                    if(!sendMessageEditText.text.toString().isNullOrEmpty()){
                                    Log.d("Messaging", "Button clicked")
                                        var tv = message("hhh", user, sendMessageEditText.text.toString())
                                        var jdata = Gson().toJson(tv)
                                        Thread(Runnable(){run {
                                            Log.d("Messaging Thread", "print t6")
                                            if(!jdata.isNullOrEmpty()){
                                                tv.uname = "You"
                                                MessageArray.add(tv)
                                                runOnUiThread(Runnable{
                                                    run {
                                                        Madapter = MessageAdapter(this@messaging, MessageArray)
                                                        MessageRecycler.layoutManager = LinearLayoutManager(this@messaging)
                                                        MessageRecycler.adapter = Madapter
                                                    }
                                                })
                                                dosS.writeUTF(jdata)
                                                dosS.flush()
                                                Thread.sleep(500)}
                                                jdata = ""
                                        }}).start()
                                    sendMessageEditText.text.clear()
                                }
                                }
                                if(EXITFLAG==1){
                                    break
                                }
                            }
                        }catch (e: Exception){
                            println(e)
                            dosS.close()
                        }
                }
                catch (e: Exception){
                    println(e)
                    sersock.close()
                }
                finally {
                    sersock.close()
                }
            }
        }).start()
    }

    fun clienttry(){
        var cs = Socket()
        cs.receiveBufferSize = 512
        cs.reuseAddress = true
        cs.bind(InetSocketAddress(9000))
        sendMessageButton = findViewById(R.id.sendMessage)
        Thread(Runnable(){
            run{
                try{
                    Log.d("Messaging Thread", "print t1")
                    println(InetAddress.getByName(GOAdd).isReachable(1000).toString() + ", server is reacheable")
                    cs.connect(InetSocketAddress(GOAdd,9000),10000)
                    var cis = DataInputStream(cs.getInputStream())
                    var cos = DataOutputStream(cs.getOutputStream())
                    var inputthread = Thread{run {
                    try{
                        Log.d("Messaging Thread", "print t2")
                        while (true) {
                            var temp = ""
                            try{
                                while (cis.available()>0){
                                temp += cis.readUTF()}}
                                catch (e:Exception){
                                    e.printStackTrace()
                                }
                                if(!temp.isNullOrEmpty()){
                                runOnUiThread(Runnable {
                                    run {
                                        Log.d("Messaginggggggggggggggg", "${temp}")
                                        var received = Gson().fromJson<message>(temp, message::class.java)
                                        MessageArray.add(received)
                                        Madapter = MessageAdapter(this@messaging, MessageArray)
                                        MessageRecycler.layoutManager = LinearLayoutManager(this@messaging)
                                        MessageRecycler.adapter = Madapter
                                    }
                                })
                                }
                            if(EXITFLAG==1){
                                break
                            }
                        }
                    }
                    catch (e:Exception){
                        println(e)
                        cis.close()
                    }
                    }}
                    inputthread.start()
                    try {
                        while (true) {
                            sendMessageButton.setOnClickListener{
                                Log.d("Messaging", user)
                                if(!sendMessageEditText.text.toString().isNullOrEmpty()){
                                var tv = message("hhh", user, sendMessageEditText.text.toString())
                                var jdata = Gson().toJson(tv)
                                Log.d("Messaging", "Send Button Clicked")
                                Thread(Runnable(){run{
                                    if(!jdata.isNullOrEmpty()){
                                        Log.d("Messaging Thread", "print t3")
                                        tv.uname = "You"
                                        MessageArray.add(tv)
                                        runOnUiThread(Runnable {
                                            run {
                                                Madapter = MessageAdapter(this@messaging, MessageArray)
                                                MessageRecycler.layoutManager = LinearLayoutManager(this@messaging)
                                                MessageRecycler.adapter = Madapter
                                            }
                                        })
                                        cos.writeUTF(jdata)
                                        cos.flush()
                                        Thread.sleep(500)}
                                    jdata = ""
                                }}).start()
                                sendMessageEditText.text.clear()
                            }
                            }
                            if(EXITFLAG==1){
                                break
                            }
                        }
                    } catch (e: Exception) {
                        cos.close()
                        println(e)
                    }
                }
                catch(e: Exception){
                    println(e)
                }
                finally {
                    println("Client Shutting Down")
                    cs.close()
                }
            }
        }).start()
    }


    val connectionInfoListener = WifiP2pManager.ConnectionInfoListener {
            info: WifiP2pInfo ->
        if(!info.groupFormed){
            Toast.makeText(this, "Peer Left, closing connection", Toast.LENGTH_LONG).show()
            Log.d("Messaging", "Save chat prompt")
            var diag = Dialog(this)
            diag.setContentView(R.layout.save_chat)
            diag.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            diag.setCancelable(false);
            diag.window?.attributes?.windowAnimations = R.anim.slide_from_left;

            var confirm = diag.findViewById<Button>(R.id.Confirm)
            var cancel = diag.findViewById<Button>(R.id.cancel)

            confirm.setOnClickListener {
                val chatname = diag.findViewById<EditText>(R.id.chat_name)
                savetodb(chatname.text.toString())
                finish()
            }
            cancel.setOnClickListener {
                diag.dismiss();
                finish()
            }
            diag.show()
        }
    }

    private fun savetodb(chatname: String){
        val dao = Chat_Database.getInstance(this).chatDao
        //var Uexfl = false
        var Cexfl = false
        var uid: String= ""
        var cid: Int =  0
        try {
            lifecycleScope.launch {
                var chats = dao.get_all_chatnames()
                println(chats)
                for (i in chats) {
                    println(i)
                    if (i.chatname == chatname) {
                        cid = i.id
                        Cexfl = true
                        break
                    }
                }
                if (!Cexfl) {
                    cid = chatname.hashCode()
                    var temp = Chat(chatname = chatname, id = cid)
                    dao.insertChat(temp)
                    Log.i("Database", "Adding chat: ${temp}")
                }
                var users = dao.get_all_usernames(user)
                println(users)
//                for (i in users) {
//                    println(i)
//                    if (i.username == user) {
//                        Uexfl = true
//                        println(uid)
//                        break
//                    }
//                }
                try{
                    for(i in MessageArray){
                        for(j in users){
                            if(j.username == i.uname){
                                uid = j.uid
                                break
                            }
                            else if(i.uname == "You"){
                                uid = "green"
                                break
                            }
                            else{
                                uid = i.uname.hashCode().toString()
                                var temp = User(uid, i.uname)
                                Log.i("Database", "Adding user: ${temp}")
                                dao.insertUser(temp)
                                break
                            }
                        }
                        dao.insertChat_line(Chat_line(cid = cid, uid = uid, line_text = i.line_text, timestamp = i.timestampp))}
                }
                catch (e:Exception){
                    e.printStackTrace()
                }
                //exitProcess(0)
                return@launch
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        finally {
            Log.d("DATABASE", "Saved chats")
        }
    }

    val peerListListener = object: WifiP2pManager.PeerListListener {
        override fun onPeersAvailable(peers: WifiP2pDeviceList?) {
            Log.d("Messagingggggg" , "Something Changed with the peers")
        }
    }

    private fun disconnect(){
        if(manager != null && channel != null){
            manager.requestGroupInfo(channel, object: WifiP2pManager.GroupInfoListener {
                override fun onGroupInfoAvailable(group: WifiP2pGroup?) {
                    if(group != null && manager != null && channel !=null){
                        manager.removeGroup(channel, object: WifiP2pManager.ActionListener{
                            override fun onSuccess() {
                                Log.d("Messaging", "removed group")
                            }
                            override fun onFailure(reason: Int) {
                                Log.d("Messaging", "failed to removed group: $reason")
                            }
                        })
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(breceiver, intentfil)
    }

    override fun onDestroy() {
        super.onDestroy()
        //disconnect()
        unregisterReceiver(breceiver)
    }

}