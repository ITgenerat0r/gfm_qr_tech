package com.example.qrcs_device

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar

class DeviceActivity : AppCompatActivity() {
    val TAG = "DeviceActivity"

    var device: MutableMap<String, String> = mutableMapOf()
    var operations: List<MutableMap<String, String>> = listOf()
//    var operations: MutableMap<String, String> = mutableMapOf()
//    lateinit var login: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)


        val toolbar: Toolbar = findViewById(R.id.toolbar_device)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val pref = SharedPreference(this)
//        login = pref.get_str("login")
        val serial_number = pref.get_int("serial_number")
        toolbar.title = serial_number.toString()

        val text_device = findViewById<TextView>(R.id.text_device_data)




        val ip = pref.get_str("server_ip")
        val port = pref.get_int("server_port")
        val cntr = Controller(ip, port)
        // get user groups here ...
        // val groups = cntr.send("getworkergroups ${pref.get_str("login")}")

        // ========== DEVICE ==================================================
        val rx = cntr.send("getdevicedata ${serial_number}")
        Log.d(TAG, rx)
        if (rx == "none" || rx == "error"){
            Log.d(TAG, "EXIT()")
            finish()
            return
        }
        for (i in rx.split('|')){
            val row = i.split(':')
            device.put(row[0], row[1])
            Log.d(TAG, "Row: ${row[0]}, ${row[1]}.")
        }
        for ((k, v) in device){
            if (text_device.text.toString().length>0){
                text_device.text = "${text_device.text} \n${k}: ${v}"
            } else {
                text_device.text = "${k}: ${v}"
            }
        }
        // ====================================================================

        // ========== OPERATIONS ==============================================
        val oper_data = cntr.send("getdeviceoperations ${serial_number}")
        if (oper_data != "none" && oper_data != "error"){
            val operation: MutableMap<String, String> = mutableMapOf()
            for(op in oper_data.split('•')){
                for (row in op.split('|')){
                    val key = row.split(':')
                    Log.d(TAG, "${key[0]}: ${key[1]}")
                    operation[key[0]] = key[1]
                }
            }
            operations += operation
        }
        // log
        for( i in operations){
            Log.d(TAG, "${i}")
        }

        // ====================================================================


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.device_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}