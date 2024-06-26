package com.example.test3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService

class WifiDreciever(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private var activity: Context): BroadcastReceiver(){

    // fun initialize(
    //     srcLooper: Looper,
    //     srcContext: Context!,
    //     listener: WifiP2pManager.ChannelListener!
    // ): WifiP2pManager.Channel!


    override fun onReceive(context: Context?, intent: Intent?) {
        val action: String? = intent?.action
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                //val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                //activity.isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
            }

            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                if (manager != null) {
                    Log.d("WifiDreciever", "Wifi Peers Changed, Calling requestPeers")
                    if(activity is messaging){
                        manager.requestPeers(channel, (activity as messaging).peerListListener)
                    }
                    //manager.requestPeers(channel, activity.peerListListener)
                    //manager.requestPeers(channel){
                    //      peers: WifiP2pDeviceList -> activity.onPeersAvailable(peers)

                    //}
                }
            }

            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                Log.i("WifiDReceiver", "WIFI P2P connection changed")
                // Respond to new connection or disconnections
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                    var conman = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//                    var netreq = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_WIFI_P2P).build()
//                    var callback =  object: ConnectivityManager.NetworkCallback(){
//                        override fun onAvailable(network: Network) {
//                            super.onAvailable(network)
//                            Log.d("WifiDReceiver", "onAvailable: Detect network");
//                            Log.d("WifiDreceiver", "Connection status changed")
//                            manager.requestConnectionInfo(channel, activity.connectionInfoListener)
//                            //manager.requestGroupInfo(channel, activity.groupInfoListener)
//                        }
//                    }
//                    conman.registerNetworkCallback(netreq, callback)
//                }
//                else{
                    manager.let { manager ->

                        Log.d("WifiDReciever", "Deprecated network callback")

                        @Suppress("DEPRECATION")
                        val networkInfo: NetworkInfo? = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)

                        @Suppress("DEPRECATION")
                        if (networkInfo?.isConnected == true) {
                            if(activity is PeersView){
                            manager.requestConnectionInfo(channel, (activity as PeersView).connectionInfoListener)
                            //manager.requestGroupInfo(channel, activity.groupInfoListener)
                        } else if(activity is messaging){
                            manager.requestConnectionInfo(channel, (activity as messaging).connectionInfoListener)

                            }
                            else{
                                //none
                            }
                        }
                        else{
                            Log.d("WifiDReciever", "disconnected")
                            if(activity is messaging){
                                manager.requestConnectionInfo(channel, (activity as messaging).connectionInfoListener)
                            }
                            else{
                                //none
                            }
                        }
                    }
                //}
                //manager.requestConnectionInfo(channel, activity.connectionInfoListener)
                //manager.requestGroupInfo(channel,activity.groupInfoListener)
            }


            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
                @Suppress("DEPRECATION")
                var device : WifiP2pDevice? = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                Log.d("WifiDReciver", "device status -- ${device?.status}")
            }
        }
    }
}
