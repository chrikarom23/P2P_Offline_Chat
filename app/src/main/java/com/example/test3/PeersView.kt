package com.example.test3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.InetAddresses
import android.net.LinkProperties
import android.net.wifi.WifiManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo
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
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.net.InetAddress
import java.util.Formatter
import kotlin.random.Random
import androidx.activity.addCallback


class PeersView : AppCompatActivity(){

    lateinit var ConvoRecyclerView: RecyclerView
    private lateinit var availableConvo: ArrayList<Convo>
    private lateinit var Cadapter: ConvoAdapter

    lateinit var manager: WifiP2pManager
    lateinit var channel: WifiP2pManager.Channel
    lateinit var activity: PeersView
    lateinit var breceiver: BroadcastReceiver
    lateinit var intentfil: IntentFilter
    lateinit var config: WifiP2pConfig

    //val peers = mutableListOf<WifiP2pDevice>()
    //lateinit var GOAdd: String
    //lateinit var deviceNameAr: String
    lateinit var deviceAr: ArrayList<WifiP2pDevice>

    private lateinit var uname: String
    var peerdevice: String ="peer"
    private val buddies = mutableMapOf<String, String>()
    val SERVERPORT = "9000"
    val instanceName = "_pares"
    val serviceType = "_presence._tcp"
    private lateinit var ipadd: String
    private var peeripadd: String = ""
    private lateinit var uidadd:String

    private lateinit var serviceReq: WifiP2pDnsSdServiceRequest
    private lateinit var servlistener: DnsSdServiceResponseListener
    private lateinit var txtlistener: DnsSdTxtRecordListener

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
        Log.i("PeersView", "Registering Reciever")
        registerReceiver(breceiver, intentfil)
        Log.i("PeersView", "Disconnecting older Connections")
        disconnect()
        Log.d("PeersView", "init completed")
        val pingpeers = findViewById<ImageButton>(R.id.pingDevices)
        pingpeers.setOnClickListener {
            Log.d("PeersView","Starting discovery")
            startreg()
        }
    }

    private fun connectInit(){
        Log.d("PeersView", "Initializing Connection")
        var toast3: Toast = Toast.makeText(this, "Connected to ", Toast.LENGTH_LONG)
        val toast4 = Toast.makeText(this, "Connection Failed", Toast.LENGTH_LONG)

//        Cadapter.onItemClick = {
//            val toast3 = Toast.makeText(this, "Clicked", Toast.LENGTH_LONG)
//            toast3.show()
//        }

        Cadapter.setOnItemClickListener(object : ItemClickListener{
            override fun onClickPosition(pos: Int) {
                Log.d("PeersView", "In ItemClickListener")
                val device: WifiP2pDevice = deviceAr[pos]
                Log.i("PeersView", "Attempting to connect to device: $device")
                //peerdevice = device.deviceName
                //uidadd = device.deviceAddress
                config = WifiP2pConfig().apply {
                    deviceAddress = device.deviceAddress
                    wps.setup = WpsInfo.PBC
                }
                if(serviceReq!=null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        manager.removeServiceRequest(channel, serviceReq, object: WifiP2pManager.ActionListener{
                            override fun onSuccess() {
                                Log.d("PeersView", "Successfully removed request")
                            }
                            override fun onFailure(reason: Int) {
                                Log.d("PeersView", "Failed to remove service request: Reason: $reason")
                            }
                        })
                    }
                }
                val random = Random(3)
                config.groupOwnerIntent=random.nextInt(14)
                //config.groupOwnerIntent = 15
                manager.connect(channel,config,object: WifiP2pManager.ActionListener{
                    override fun onSuccess() {
                        toast3.setText("Connected to ${device.deviceName} : ${device.deviceAddress}")
                        toast3.show()
                    }

                    override fun onFailure(reason: Int) {
                        toast4.setText("Connection Failed:- reason:$reason")
                        toast4.show()
                    }
                })
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_peers_view)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        uname = intent.getStringExtra("Uname").toString()
        val random = (1..1000).shuffled().first()
        if(uname.isNullOrEmpty()){
            uname = "Peer "+"${random}"
        }
        supportActionBar?.setTitle("Welcome, $uname")
        onBackPressedDispatcher.addCallback(this, object:  OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.d("PeersView", "Back pressed, Disconnecting from peers")
                disconnect()
                finish()
            }
        })

        Log.d("PeersView", "onCreate peers view")


        availableConvo = ArrayList<Convo>()
        availableConvo.clear()
        deviceAr = ArrayList()
        Cadapter = ConvoAdapter(availableConvo)
        ConvoRecyclerView = findViewById<RecyclerView>(R.id.convoRecyclerView)
        ConvoRecyclerView.layoutManager = LinearLayoutManager(this)
        ConvoRecyclerView.adapter = Cadapter
        init()

    }

    val connectionInfoListener = WifiP2pManager.ConnectionInfoListener {
            info: WifiP2pInfo ->
        val intent = Intent(this@PeersView, messaging::class.java)
        val groupOwnerAddress: String? = info.groupOwnerAddress.hostAddress
        Log.d("PeersViewConn", "GroupOwnerAdress: $groupOwnerAddress")
        if(info.groupFormed && info.isGroupOwner){
            Log.d("PeersViewConn","Group formed and current device is Host :$groupOwnerAddress")
            val intent = Intent(this@PeersView, messaging::class.java)
            intent.putExtra("GO", groupOwnerAddress)
            intent.putExtra("igo", info.isGroupOwner)
            intent.putExtra("user", uname)
            //intent.putExtra("uid", uidadd)
            startActivity(intent)
        }
        else if(info.groupFormed){
            Log.d("PeersViewConn","Group formed and current device is guest")
            intent.putExtra("GO", groupOwnerAddress)
            intent.putExtra("user", uname)
            //intent.putExtra("uid", uidadd)
            startActivity(intent)
        }
    }


    private fun startreg(){
        var t = Toast.makeText(this, "Encountered a problem while starting the service", Toast.LENGTH_SHORT)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val connman= applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            val link: LinkProperties = connman.getLinkProperties(connman.activeNetwork) as LinkProperties
//            ipadd = link.linkAddresses[1].toString()
//        } else {
//            Log.d("PeersView", "Lower API using deprecated stuff")
//            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//            @Suppress("DEPRECATION")
//            ipadd = android.text.format.Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
//        }
        val record: Map<String, String> = mapOf("buddyname" to uname, "visibility" to "public")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val serviceinfo = WifiP2pDnsSdServiceInfo.newInstance(instanceName,serviceType, record)
            manager.addLocalService(channel, serviceinfo, object: WifiP2pManager.ActionListener{
                override fun onSuccess() {
                    Log.d("PeersView" ,"Registered Local wifip2p service")
                }

                override fun onFailure(reason: Int) {
                    Log.d("PeersView", "Local service registration failed to reason: $reason")
                    t.show()
                }
            })
        } else {
            TODO("VERSION.SDK_INT < JELLY_BEAN")
        }
        Toast.makeText(this@PeersView, "Started discovery", Toast.LENGTH_SHORT).show()
        discoverService()
    }

    fun discoverService(){
        //ASK FOR PRIVACY IN MAIN SCREEN
        var privacy: Boolean = false
        txtlistener = DnsSdTxtRecordListener{
                fullDomainName, txtRecordMap, srcDevice ->
            Log.i("PeersView", "DnsSdTxt Record available")
            Log.i("PeersView", "${txtRecordMap["buddyname"]}")
            Log.i("PeersView", "${txtRecordMap["visibility"]}")
            txtRecordMap["buddyname"]?.also { buddies[srcDevice.deviceName] = it}
            if(txtRecordMap["visibility"] == "private")
                privacy = true
            else
                privacy = false
        }

        servlistener = DnsSdServiceResponseListener{
                instanceName, registrationType, srcDevice ->
            srcDevice.deviceName = buddies[srcDevice.deviceName] ?: srcDevice.deviceName
            deviceAr.add(srcDevice)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                availableConvo.removeIf{it.deviceName == srcDevice.deviceName}
            }
            else{
                availableConvo.clear()
            }
            availableConvo.add(Convo(srcDevice.deviceName, 10, privacy))
            Cadapter = ConvoAdapter(availableConvo)
            ConvoRecyclerView.adapter = Cadapter
            Log.d("PeersView", "in servlistener, got device: $srcDevice")
            connectInit()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            manager.setDnsSdResponseListeners(channel, servlistener, txtlistener)
            serviceReq = WifiP2pDnsSdServiceRequest.newInstance()
            manager.addServiceRequest(channel,serviceReq, object: WifiP2pManager.ActionListener{
                override fun onSuccess() {
                    Log.d("PeersView", "service request added")
                }

                override fun onFailure(reason: Int) {
                    Log.d("PeersView", "service request adding failed, reason: $reason")
                }
            })
            manager.discoverServices(channel, object: WifiP2pManager.ActionListener{
                override fun onSuccess() {
                    Log.d("PeersView", "Success!!!")
                }

                override fun onFailure(reason: Int) {
                    when(reason){
                        WifiP2pManager.P2P_UNSUPPORTED->{
                            Log.d("PeersView", "Wi-Fi Direct isn't supported on this device.")
                        }
                    }
                }
            })
        }
    }

    private fun disconnect(){
    if(manager != null && channel != null){
        manager.requestGroupInfo(channel, object: WifiP2pManager.GroupInfoListener {
            override fun onGroupInfoAvailable(group: WifiP2pGroup?) {
                if(group != null && manager != null && channel !=null){
                    manager.removeGroup(channel, object: WifiP2pManager.ActionListener{
                        override fun onSuccess() {
                            Log.d("PeersView", "removed group")
                        }
                        override fun onFailure(reason: Int) {
                            Log.d("PeersView", "failed to removed group: $reason")
                        }
                    })
                }
            }
        })
    }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_s,menu)
        Log.i("PeersView", "Create options menu")
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

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
        //disconnect()
        //breceiver = WifiDreciever(manager, channel, this)
        //unregisterReceiver(breceiver)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(breceiver, intentfil)

    }

    override fun onDestroy() {
        super.onDestroy()
        disconnect()
        unregisterReceiver(breceiver)
    }
}