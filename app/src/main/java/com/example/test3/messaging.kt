package com.example.test3

import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.Buffer
import java.util.Calendar
import java.util.concurrent.Executors

class messaging : AppCompatActivity(){

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var sendMessageEditText: EditText
    private lateinit var sendMessageButton: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Log.d("MessagingView", "In messaging Activity")
        var igo = intent.getBooleanExtra("igo", false)
        var GOAdd = intent.getStringExtra("GO")
        //var ipadd = intent.getStringExtra("ipadd")
        Toast.makeText(this, "is group owner? = $igo", Toast.LENGTH_LONG).show()
        Toast.makeText(this, "GO Address = $GOAdd", Toast.LENGTH_LONG).show()
        //Toast.makeText(this, "ipaddress = $ipadd", Toast.LENGTH_SHORT).show()
        setContentView(R.layout.activity_chatview)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        //val bundle = arguments
        //val message = bundle!!.getString("")

        messageRecyclerView = findViewById(R.id.messageRecyclerView)
        sendMessageButton = findViewById(R.id.sendMessage)
        sendMessageEditText = findViewById(R.id.sendMessageEditText)

//        if(igo){
//            var sersock = ServerSocket(9000)
//            var ss = sersock.accept()
//            var dosS = DataOutputStream(ss.getOutputStream())
//            dosS.writeUTF("TESTESTESTESTES")
//            dosS.flush()
//        }
//        else{
//            var cs = Socket()
//            cs.bind(null)
//            cs.connect(InetSocketAddress("192.168.49.1",9000))
//        }

//        if(igo){
//            Thread(Runnable(){
//                run {
//                    runOnUiThread(Runnable {
//                        var server:Serverclass = Serverclass()
//                        server.start()
//                    })
//                }
//            }).start()
//        }
//        else{
//            Thread(Runnable(){
//                run {
//                    runOnUiThread(Runnable {
//                        var client = ClientClass(GOAdd)
//                        client.start()
//                        client.write("testestestestes".toByteArray())
//                    })
//                }
//            }).start()
//        }
    }





    class Serverclass: Thread(){
        lateinit var serverSocket: ServerSocket
        lateinit var inputStream: InputStream
        lateinit var outputStream: OutputStream
        lateinit var socket: Socket
        override fun run() {
            try {
                serverSocket = ServerSocket(9000)
                socket = serverSocket.accept()
                inputStream = socket.getInputStream()
                outputStream = socket.getOutputStream()

            }
            catch(ex:IOException){
                ex.printStackTrace()
            }
            val executors = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            executors.execute(Runnable {
                kotlin.run {
                    val buffer = ByteArray(1024)
                    var byte:Int
                    while(true){
                        try{
                            byte = inputStream.read(buffer)
                            if(byte > 0){
                                var finalByte = byte
                                handler.post(Runnable{
                                    kotlin.run {
                                        var tmpMessage = String(buffer,0,finalByte)
                                        Log.i("Server class","$tmpMessage")
                                    }
                                })
                            }
                        }
                        catch(ex:IOException){
                            ex.printStackTrace()
                        }
                    }
                }
            })
            fun write(byteArray: ByteArray){
                try {
                    Log.i("Server write","$byteArray sending")
                    outputStream.write(byteArray)
                }catch (ex:IOException){
                    ex.printStackTrace()
                }
            }
        }
    }

    class ClientClass(hostAddress: String?): Thread() {

        var hostAddress: String = "192.168.49.1"
        lateinit var inputStream: InputStream
        lateinit var outputStream: OutputStream
        lateinit var socket: Socket

        fun write(byteArray: ByteArray){
            try {
                outputStream.write(byteArray)
            }catch (ex:IOException){
                ex.printStackTrace()
            }
        }

        override fun run() {
            try {
                socket = Socket()
                socket.connect(InetSocketAddress(hostAddress,9000),10000)
                inputStream = socket.getInputStream()
                outputStream = socket.getOutputStream()
            }catch (ex:IOException){
                ex.printStackTrace()
            }
            val executor = Executors.newSingleThreadExecutor()
            var handler =Handler(Looper.getMainLooper())

            executor.execute(kotlinx.coroutines.Runnable {
                kotlin.run {
                    val buffer =ByteArray(1024)
                    var byte:Int
                    while (true){
                        try{
                            byte = inputStream.read(buffer)
                            if(byte>0){
                                val finalBytes = byte
                                handler.post(Runnable{
                                    kotlin.run {
                                        val tmpMeassage = String(buffer,0,finalBytes)

                                        Log.i("client class", tmpMeassage)
                                    }
                                })
                            }
                        }catch (ex:IOException){
                            ex.printStackTrace()
                        }
                    }
                }
            })
        }

    }
}