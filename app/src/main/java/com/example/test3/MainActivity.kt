package com.example.test3

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ClipData.Item
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import java.nio.channels.InterruptedByTimeoutException


class MainActivity : AppCompatActivity() {

    fun initializer(){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializer()

        val uname = findViewById<EditText>(R.id.uname)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        val pbt1 = findViewById<Button>(R.id.Peerbutton)
        pbt1.setOnClickListener{
            val intent = Intent(this@MainActivity, PeersView::class.java)
            intent.putExtra("Uname", uname.text.toString())
            startActivity(intent)
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
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val intent2 = Intent(this, MainActivity::class.java)
        startActivity(intent2)
    }
}