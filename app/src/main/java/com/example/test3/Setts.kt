package com.example.test3

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.room.Dao
import com.google.gson.Gson
import io.ipfs.api.IPFS
import io.ipfs.api.MerkleNode
import io.ipfs.api.NamedStreamable
import io.ipfs.multiaddr.MultiAddress
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Setts:AppCompatActivity() {
    private lateinit var dao: ChatDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setTitle("Settings")


        //val wifiBtn = findViewById<Button>(R.id.button)
        val Fileexport = findViewById<Button>(R.id.button2)
        val IPFSBtn = findViewById<Button>(R.id.button3)
        val DataBtn = findViewById<Button>(R.id.button4)
        //val AboutBtn = findViewById<Button>(R.id.button5)

        DataBtn.setOnClickListener{
            dao = Chat_Database.getInstance(this).chatDao
            val builder = AlertDialog.Builder(this@Setts)
            builder.setTitle("Chat Deletion Confirmation")
            builder.setMessage("Are you sure of your actions? The data being purged is irrecoverable.")
            builder.setIcon(R.drawable.baseline_warning_24)
            builder.setPositiveButton("YES"){
                dialog,id ->
                cleardata()
            }
            builder.setNegativeButton("CANCEL"){
                dialog,id ->
                Toast.makeText(this, "Deletion Canceled", Toast.LENGTH_LONG).show()
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        Fileexport.setOnClickListener{
            Log.d("setts", "File export clicked")
            db_exporter()
        }

        IPFSBtn.setOnClickListener {
//            db_exporter()
            ipfs()
        }

        }

    fun cleardata(){
        dao = Chat_Database.getInstance(this).chatDao
        lifecycleScope.launch {
            Log.i("Settings",  "Deleting all data")

            dao.deletechats()
            dao.deletechatdata()
        }
    }

    fun db_exporter(){
        Log.d("setts", "File export clicked")
        val dao = Chat_Database.getInstance(this).chatDao
        GlobalScope.launch {
            try {
                Log.d("setts", "creating global scope thread")
                Log.d("setts", "Trying to create file")
                val dir = File(this@Setts.filesDir, "pares")
                if(!dir.exists()){
                    dir.mkdir();
                }
                var chatnames = Gson().toJson(dao.get_all_chatnames())
                var users = Gson().toJson(dao.get_all_usernames())
                var chatlines = Gson().toJson(dao.get_all_chatlines())
                var curdate = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM, Locale.UK).format(
                    Date(Date().time)
                )

                Log.d("setts", "Creating json data of chat")
                var consolidated = curdate +"\n\n\n"+ chatnames+"\n\n\n"+users+"\n\n\n"+chatlines

                try{
                    Log.d("setts", "Writing to file")
                    var fio = File(dir, "sqldata.txt")
                    var fwr = FileWriter(fio, false)
                    fwr.append(consolidated)
                    fwr.flush()
                    fwr.close()
                }
                catch (e:Exception){
                    e.printStackTrace()
                    Toast.makeText(this@Setts, "Failed to create file", Toast.LENGTH_LONG).show()
                }

            }
            catch (e:Exception){
                println("Caught an exception: $e")
            }
            finally {
                Log.d("setts", "Exiting db_exporter")
            }
            return@launch
        }
        Toast.makeText(this@Setts, "Created file", Toast.LENGTH_LONG).show()
    }

    fun ipfs(){
        val IPFS_INFURA_URL = MultiAddress("/dnsaddr/ipfs.infura.io/tcp/5001/https")
        db_exporter()
        Thread(Runnable(){
            run{
                lateinit var ipfsClient: IPFS
                var dir = File(this@Setts.filesDir, "pares")

                try{
                    ipfsClient = IPFS(IPFS_INFURA_URL)
                }
                catch (e:Exception){
                    Log.d("setts", "couldnt open node")
                e.printStackTrace()
                }
                var fio = File(dir, "sqldata.txt")
                var ba = fio.readBytes()
                val file = NamedStreamable.ByteArrayWrapper("sqldata.txt", ba)
                try{
                    var addresult: MerkleNode = ipfsClient.add(file).get(0)
                    Log.d("IPFS Docu hash", addresult.hash.toBase58())
                }
                catch (e:Exception){
                    e.printStackTrace()
                }
            }}).start()
    }
}