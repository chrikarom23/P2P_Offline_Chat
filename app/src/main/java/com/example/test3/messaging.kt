package com.example.test3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class messaging : AppCompatActivity(){

    lateinit var MessageRecycler: RecyclerView
    private lateinit var MessageArray: ArrayList<message>
    private lateinit var Madapter: MessageAdapter
    private lateinit var sendMessageEditText: EditText
    private lateinit var sendMessageButton: FloatingActionButton

    private lateinit var manager: WifiP2pManager
    private lateinit var channel:WifiP2pManager.Channel
    private lateinit var breceiver:BroadcastReceiver
    private var GOAdd: String? = "192.168.49.1"
    private lateinit var user: String
    private lateinit var peer: String

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Log.d("MessagingView", "In messaging Activity")
        var igo = intent.getBooleanExtra("igo", false)
        GOAdd = intent.getStringExtra("GO")
        user = intent.getStringExtra("user") ?: "you"
        peer = intent.getStringExtra("peer") ?: "peer"
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
                Log.d("PeersView", "Back pressed, Disconnecting from peers")
                disconnect()
                finish()
            }
        })

        MessageArray = arrayListOf()

        var intentfil = IntentFilter()
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
                    Log.d("Messaging", "in thread")
                    var ss = sersock.accept()
                    var dosS = DataOutputStream(ss.getOutputStream())
                    var dis = DataInputStream(ss.getInputStream())
                    val inputthread = Thread{
                        run{
                            try{
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
                                    Log.d("Messaging", "Button clicked")
                                        var tv = message("hhh", user, sendMessageEditText.text.toString())
                                        var jdata = Gson().toJson(tv)
                                        Thread(Runnable(){run {
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
                        }catch (e: Exception){
                            println(e)
                            dosS.close()
                        }
                }
                catch (e: Exception){
                    println(e)
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
                    println(InetAddress.getByName(GOAdd).isReachable(1000).toString() + ", server is reacheable")
                    cs.connect(InetSocketAddress(GOAdd,9000),10000)
                    var cis = DataInputStream(cs.getInputStream())
                    var cos = DataOutputStream(cs.getOutputStream())
                    var inputthread = Thread{run {
                    try{
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
                                        var tv = message("hhh", user, sendMessageEditText.text.toString())
                                        var jdata = Gson().toJson(tv)
                                        Log.d("Messaging", "Send Button Clicked")
                                        //println("helooooooooooooooo")
                                        Thread(Runnable(){run{
                                            if(!jdata.isNullOrEmpty()){
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

                            //MessageArray.remove(tv)
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
        Log.d("Messagingggggggg", "printing this shitttt : ${info.groupOwnerAddress}")
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

}