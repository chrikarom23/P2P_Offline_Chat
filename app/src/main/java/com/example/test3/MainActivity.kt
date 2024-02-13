package com.example.test3

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.log
import kotlin.system.exitProcess
import android.Manifest.permission as perm


class MainActivity : AppCompatActivity() {

    companion object {
        private const val WIFI_PERMISSION_CODE = 500
        private const val LOC_PERMISSION_CODE = 501
        //private const val NEARBY_PERMISSION_CODE = 502
    }

    private var cancellationSignal: CancellationSignal? = null
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() = @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback() {

            // here we need to implement two methods
            // onAuthenticationError and onAuthenticationSucceeded
            // If the fingerprint is not recognized by the app it will call
            // onAuthenticationError and show a toast
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                if(errorCode == BiometricPrompt.BIOMETRIC_ERROR_NO_BIOMETRICS){
                    Toast.makeText(this@MainActivity, "Use biometric authentication for increased Security..", Toast.LENGTH_LONG).show()
                    return Unit
                }
                else{
                notifyUser("Authentication Error : $errString")
                exitProcess(0)
                }
            }

            // If the fingerprint is recognized by the app then it will call
            // onAuthenticationSucceeded and show a toast that Authentication has Succeed
            // Here you can also start a new activity after that
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                notifyUser("Authentication Succeeded")

                // or start a new Activity

            }
        }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was Cancelled by the user")
            exitProcess(0)
        }
        return cancellationSignal as CancellationSignal
    }

    // it checks whether the app the app has fingerprint permission
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isDeviceSecure) {
            notifyUser("Fingerprint authentication has not been enabled in settings")
            return false
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint Authentication Permission is not enabled")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    }

    // this is a toast method which is responsible for showing toast
    // it takes a string as parameter
    private fun notifyUser(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkBiometricSupport()
        }

        Log.i("Main", "crossed check")
        val biometricPrompt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.i("Main", "In prompt creation")
            BiometricPrompt.Builder(this)
                .setTitle("BioMetric Authentication")
                .setSubtitle("Ensure your identity")
                .setDescription("Fingerprint")
                .setNegativeButton("Cancel", this.mainExecutor, DialogInterface.OnClickListener { dialog, which ->
                    notifyUser("Authentication Cancelled")
                    exitProcess(0)
                }).build()
        } else {
            TODO("VERSION.SDK_INT < P")
        }

        // start the authenticationCallback in mainExecutor
        biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
        //initializer()
        setContentView(R.layout.activity_main)

        val uname = findViewById<EditText>(R.id.uname)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        val pbt1 = findViewById<Button>(R.id.Peerbutton)
        pbt1.setOnClickListener{
            checkPermission(perm.ACCESS_WIFI_STATE, WIFI_PERMISSION_CODE)
            checkPermission(perm.ACCESS_COARSE_LOCATION, LOC_PERMISSION_CODE)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                checkPermission(perm.NEARBY_WIFI_DEVICES, NEARBY_PERMISSION_CODE)
//            }
            if(ContextCompat.checkSelfPermission(this@MainActivity, perm.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this@MainActivity, perm.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        val intent = Intent(this@MainActivity, PeersView::class.java)
                        intent.putExtra("Uname", uname.text.toString())
                        startActivity(intent)
                    }
                }
        }

        val bt2 = findViewById<Button>(R.id.Exitapp)
        bt2.setOnClickListener{
            val builder = AlertDialog.Builder(this@MainActivity)
            builder.setMessage("Quit Application?")
                .setCancelable(false)
                .setPositiveButton("Yes"){
                    dialog,id->
                    exitProcess(0)
                }
                .setNegativeButton("No") {
                    dialog,id->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        val bt3 = findViewById<Button>(R.id.Viewchat)
        bt3.setOnClickListener{
            val intent = Intent(this@MainActivity, Conversations::class.java)
            startActivity(intent)
        }


    }

    private fun checkPermission(permission: String, requestCode: Int){
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this@MainActivity, "Permission already granted: $permission", Toast.LENGTH_SHORT).show()
        } else if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
            val builder:AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
            builder.setMessage("This app requires $permission for WiFi Direct Capabilities. Location is not being recorded or stored in anyway.")
                .setTitle("Permission Required!")
                .setCancelable(false)
                .setPositiveButton("Enable Permissions"){
                        dialog, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode )
                    dialog.dismiss()
                }
                .setNegativeButton("Exit Application"){
                        dialog, _ ->
                    dialog.dismiss()
                    //exitProcess(0)
                }
            builder.show()
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WIFI_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "WIFI Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else if(!ActivityCompat.shouldShowRequestPermissionRationale(this, perm.ACCESS_WIFI_STATE)){
                val builder:AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage("This app requires WiFi inorder to function.\n" + "Please allow WiFi from settings to proceed further.")
                    .setTitle("Permission Required!!")
                    .setCancelable(false)
                    .setNegativeButton("Cancel"){
                            dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Settings"){
                            dialog, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.setData(uri)
                        startActivity(intent)

                        dialog.dismiss()
                    }
                builder.show()
            }
            else {
                Toast.makeText(this@MainActivity, "WIFI Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (requestCode == LOC_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "LOCATION Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else if(!ActivityCompat.shouldShowRequestPermissionRationale(this, perm.ACCESS_COARSE_LOCATION)){
                val builder:AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage("This app requires Location Permission inorder for WiFi Direct to function.\n" + "Please allow Location from settings to proceed further.")
                    .setTitle("Permission Required!!")
                    .setCancelable(false)
                    .setNegativeButton("Cancel"){
                            dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Settings"){
                            dialog, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.setData(uri)
                        startActivity(intent)

                        dialog.dismiss()
                    }
                builder.show()
            }
            else {
                Toast.makeText(this@MainActivity, "Location Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
//        else if (requestCode == NEARBY_PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this@MainActivity, "LOCATION Permission Granted", Toast.LENGTH_SHORT)
//                    .show()
//            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(
//                    this,
//                    perm.NEARBY_WIFI_DEVICES
//                )
//            ) {
//                val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
//                builder.setMessage("This app requires Location Permission inorder for WiFi Direct to function.\n" + "Please allow Location from settings to proceed further.")
//                    .setTitle("Permission Required!!")
//                    .setCancelable(false)
//                    .setNegativeButton("Cancel") { dialog, _ ->
//                        dialog.dismiss()
//                    }
//                    .setPositiveButton("Settings") { dialog, _ ->
//                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                        val uri = Uri.fromParts("package", packageName, null)
//                        intent.setData(uri)
//                        startActivity(intent)
//
//                        dialog.dismiss()
//                    }
//                builder.show()
//            } else {
//                Toast.makeText(this@MainActivity, "Location Permission Denied", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
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
}