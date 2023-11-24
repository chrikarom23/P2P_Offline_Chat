package com.example.test3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



class PeersView : AppCompatActivity() {

    private lateinit var ConvoRecyclerView: RecyclerView
    private lateinit var availableConvo: ArrayList<Convo>
    private lateinit var Cadapter: ConvoAdapter

    lateinit var manager: WifiP2pManager
    lateinit var channel: WifiP2pManager.Channel
    lateinit var activity: PeersView
    lateinit var breceiver: BroadcastReceiver
    lateinit var intentfil: IntentFilter

    //var peers = ArrayList<WifiP2pDevice>()
    val peers = mutableListOf<WifiP2pDevice>()
    lateinit var deviceNameAr: String
    lateinit var deviceAr: Array<WifiP2pDevice>


    private fun init() {
        //val intent = Intent(TODO())
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)
        breceiver = WifiDreciever(manager, channel, this)

        intentfil = IntentFilter()
        intentfil.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentfil.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentfil.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentfil.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)


        //var WifiPeerList = ArrayList<WifiP2pDeviceList>[]

    }

    private fun discovery(){
        //var uname = findViewById<TextView>(R.id.)
        //uname.text = intent.extras?.getString("uname")?: ""
        val pingpeers = findViewById<ImageButton>(R.id.pingDevices)
        val toast1 = Toast.makeText(this,"Discovery Started", Toast.LENGTH_LONG)
        val toast2 = Toast.makeText(this,"Discovery Failed", Toast.LENGTH_LONG)

        pingpeers.setOnClickListener{
            manager.discoverPeers(channel, object : WifiP2pManager.ActionListener{
                override fun onSuccess() {
                    toast1.show()
                }

                override fun onFailure(reason: Int) {
                    toast2.show()
                }

            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peers_view)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        init()
        discovery()


        availableConvo = ArrayList()
        Cadapter = ConvoAdapter(this, availableConvo)


        ConvoRecyclerView = findViewById<RecyclerView>(R.id.convoRecyclerView)
        ConvoRecyclerView.layoutManager = LinearLayoutManager(this)
        ConvoRecyclerView.adapter = Cadapter


    }

    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if(refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            deviceNameAr = peerList.deviceList.toString()
            var index = 0
            for (i in peerList.deviceList) {
                deviceAr[index] = i
                index++
            }
            Log.d("Mine", peerList.deviceList.toString())
        }

        if(peers.isEmpty()){
            Log.d("Mine", "No peers found")
            val toast = Toast.makeText(this,"No Matching Peers Nearby", Toast.LENGTH_SHORT)
            toast.show()
        }
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

    override fun onPause() {
        super.onPause()
        breceiver = WifiDreciever(manager, channel, this)
        unregisterReceiver(breceiver)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(breceiver, intentfil)

    }
}