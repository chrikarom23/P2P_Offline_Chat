package com.example.test3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class PeersView : AppCompatActivity(){

    lateinit var ConvoRecyclerView: RecyclerView
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
    lateinit var deviceAr: ArrayList<WifiP2pDevice>
    //lateinit var config : WifiP2pConfig


    private fun init() {

        Log.d("PeersView", "In init function")
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)
        breceiver = WifiDreciever(manager, channel, this)

        intentfil = IntentFilter()
        intentfil.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentfil.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentfil.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentfil.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        registerReceiver(breceiver, intentfil);
        Log.d("PeersView", "init completed")
        //add service discovery later
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            addServiceRequest()
//        }
        discovery()
        //var WifiPeerList = ArrayList<WifiP2pDeviceList>[] 9
    }

    private fun discovery() {
        //var uname = findViewById<TextView>(R.id.)
        //uname.text = intent.extras?.getString("uname")?: ""
        val pingpeers = findViewById<ImageButton>(R.id.pingDevices)

        Log.d("PeersView", "In discovery function")
        val toast1 = Toast.makeText(this, "Discovery Started", Toast.LENGTH_LONG)
        val toast2 = Toast.makeText(this, "Discovery Failed", Toast.LENGTH_LONG)

        //add service discovery later
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            discoverService()
//        }
//        else {
        pingpeers.setOnClickListener {
                manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        toast1.show()
                    }

                    override fun onFailure(reason: Int) {
                        toast2.show()
                    }

                })
            }
            Log.d("PeersView", "Checking for connection")
        //}
    }

    private fun connectInit(){
        Log.d("PeersView", "Initializing Connection")
        var toast3: Toast = Toast.makeText(this, "Connected to ", Toast.LENGTH_LONG)
        val toast4 = Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG)

//        Cadapter.onItemClick = {
//            val toast3 = Toast.makeText(this, "Clicked", Toast.LENGTH_LONG)
//            toast3.show()
//        }
//
//        Cadapter.onItemClick = {
//            val toast3 = Toast.makeText(this, "Clicked", Toast.LENGTH_LONG)
//            toast3.show()
//        }

        Cadapter.setOnItemClickListener(object : ItemClickListener{
            override fun onClickPosition(pos: Int) {
                Log.d("PeersView", "In ItemClickListener")
                val device : WifiP2pDevice = deviceAr[pos]
                Log.i("PeersView", "Attempting to connect to device: $device")
                var config: WifiP2pConfig = WifiP2pConfig()
                config.deviceAddress = device.deviceAddress
                manager.connect(channel,config,object: WifiP2pManager.ActionListener{
                    override fun onSuccess() {
                        toast3.setText("Connected to $device")
                        toast3.show()
                        val intent = Intent(this@PeersView, messaging::class.java)
                        startActivity(intent)
                    }

                    override fun onFailure(reason: Int) {
                        toast4.show()
                    }
                })
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    open fun addServiceRequest(): Unit
    {
        var req = WifiP2pDnsSdServiceRequest.newInstance("pare", "_http._tcp")
        manager.addServiceRequest(channel,req,object: WifiP2pManager.ActionListener{
            override fun onSuccess() {
                Log.d("PeersView", "Added Request $req")
            }

            override fun onFailure(reason: Int) {
                Log.d("PeersView", "Failed to add request :(")
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    open fun discoverService():Unit{
        manager.discoverServices(channel,object: WifiP2pManager.ActionListener{
            override fun onSuccess() {
                Log.d("PeersView", "Discovered SS Request")
            }

            override fun onFailure(reason: Int) {
                Log.d("PeersView", "Failed to Discover SS request :(")
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peers_view)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        var uname: String = intent.getStringExtra("Uname").toString()
        supportActionBar?.setTitle("Welcome, $uname")

        Log.d("PeersView", "onCreate peers view")

        availableConvo = ArrayList<Convo>()
        Cadapter = ConvoAdapter(availableConvo)
        ConvoRecyclerView = findViewById<RecyclerView>(R.id.convoRecyclerView)
        ConvoRecyclerView.layoutManager = LinearLayoutManager(this)
        ConvoRecyclerView.adapter = Cadapter
        init()

    }


    val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if(refreshedPeers != peers) {
            peers.clear()
            availableConvo.clear()
            peers.addAll(refreshedPeers)
            Log.d("PeersView", "In peerListListener")

            deviceNameAr = peerList.deviceList.toString()
            deviceAr = arrayListOf<WifiP2pDevice>()

            var index = 0
            for (i in peerList.deviceList) {
                deviceAr.add(i)
                Log.i("PeersView", "Adding device to deviceAr: $deviceAr")
                availableConvo.add(Convo(
                    (i.deviceName.toString()),
                    i.deviceAddress.toString(),
                    (index%2==0)
                ))
                index++
                Log.d("PeersView", "Looping through device names: $i")
            }
            Log.d("PeersView", "Out of Loop")

            Cadapter = ConvoAdapter(availableConvo)
            ConvoRecyclerView.adapter = Cadapter
            connectInit()
            //(Cadapter as WiFiPeerListAdapter
//            adapter.onItemClick = {
//                Log.d("PeersView", "Item Clicked")
//                val toast3 = Toast.makeText(this, "Clicked", Toast.LENGTH_LONG)
//                toast3.show()
//            }
        }
        if(peers.isEmpty()){
            Log.d("Mine", "No peers found")
            val toast = Toast.makeText(this,"No Matching Peers Nearby", Toast.LENGTH_SHORT)
            toast.show()
        }
//        val device : WifiP2pDevice = deviceAr[0]
//        Log.i("PeersView", "Testing Connection with device 0")
//        var conf: WifiP2pConfig = WifiP2pConfig()
//        conf.deviceAddress = device.deviceAddress
//        manager.connect(channel,conf,object: WifiP2pManager.ActionListener{
//            override fun onSuccess() {
//                Log.i("PeersView", "Connected to device 0")
//            }
//
//            override fun onFailure(reason: Int) {
//                Log.i("PeersView", "Cant Connect to device 0")
//            }
//        })
    }

    val connectionInfoListener = WifiP2pManager.ConnectionInfoListener {
        info: WifiP2pInfo ->
        val groupOwnerAddress: String? = info.groupOwnerAddress.hostAddress
        if(info.groupFormed && info.isGroupOwner){
                Log.d("PeersView","Group formed and current device is Host")
        }
        else if(info.groupFormed){
            Log.d("PeersView","Group formed and current device is guest")
       }
    }

//    class ClientClass: Thread{
//        var HostAdd: String
//        private lateinit var inputstream : InputStream
//        private lateinit var outputstream : OutputStream
//        constructor(hostaddress: InetAddress){
//            HostAdd = hostaddress.hostAddress.toString()
//        }
//
//        override fun run() {
//            val socket = Socket()
//            try {
//                socket.connect(InetSocketAddress(HostAdd, 8888), 500)
//                inputstream = socket.getInputStream()
//                outputstream = socket.getOutputStream()
//            }
//            catch (e: IOException){
//                e.printStackTrace()
//            }
//            lateinit var executor: ExecutorService
//            lateinit var handler: Handler
//            executor = Executors.newSingleThreadExecutor()
//            handler = Handler(Looper.getMainLooper())
//
//            executor.execute(Runnable {
//                var buffer: ByteArray
//                var bytes: Int
//
//                while(socket!=null){
//                    bytes = inputstream.read(buffer)
//                    if(bytes>0) run {
//                        var finalbytes: Int = bytes
//                        handler.post(Runnable {
//                            var tempmsg: String = String(buffer, 0, finalbytes)
//                        })
//                    }
//                }
//            })
//        }
//    }
//

//    val connlistener = WifiP2pManager.ConnectionInfoListener {
//            info ->
//        val groupOwnerAddress: String? = info.groupOwnerAddress.hostAddress
//        if(info.groupFormed && info.isGroupOwner){
//
//        }
//        else if(info.groupFormed){
//
//        }
//    }
/*
    fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }
*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_s,menu)
        Log.i("PeersView", "Create options menu")
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_hmmm -> {
            // User chooses the "Settings" item. Show the app settings UI.
            Log.i("PeersView", "Clicked Second option")
            true
        }

        R.id.action_settings -> {
            // User chooses the "Favorite" action. Mark the current item as a
            // favorite.
            Log.i("PeersView", "Clicked Settings")
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(breceiver)
    }

    class ClientClass(target: Runnable?) : Thread(target) {
        lateinit var hostadd: String;
        lateinit var inputStream: InputStream;
        lateinit var outputStream: OutputStream;
    }
}