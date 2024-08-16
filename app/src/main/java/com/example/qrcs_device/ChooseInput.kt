package com.example.qrcs_device

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar



class ChooseInput : AppCompatActivity() {
    
    lateinit var txt_status: TextView
    lateinit var btn_add: Button
    lateinit var input_number: TextView
    

    fun logout(){
        val preferences = SharedPreference(this)
        preferences.set_str("passwd", "")
        preferences.set_str("username", "")
        val intent = Intent(this, Auth::class.java)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val TAG = "ChooseInput"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_input)
        
        txt_status = findViewById(R.id.textView_status)
        btn_add = findViewById(R.id.btn_add_device)
        btn_add.visibility = View.INVISIBLE


        val btn_qr = findViewById<Button>(R.id.btn_read_qr)
        btn_qr.setOnClickListener {
            val pref = SharedPreference(this)
            pref.set_str("qr_code", "")
            // run QR reader here

            //
            val qr_data = pref.get_str("qr_code") // need handler or in onResume()
            // set qr_data to input field
        }
        val pref = SharedPreference(this)
        val btn_apply = findViewById<Button>(R.id.btn_apply)
        btn_apply.setOnClickListener {
            input_number = findViewById<EditText>(R.id.input_serial_number)
            var serial_number = 0

            if(input_number.text.toString() != ""){
                serial_number = input_number.text.toString().toInt()


                pref.set_int("serial_number", serial_number)
                val intent = Intent(this, DeviceActivity::class.java)
                startActivity(intent)
            }

            Log.d(TAG, "Number: ${serial_number}")

        }

        val toolbar: Toolbar = findViewById(R.id.toolbar_choose_input)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
//        actionBar?.setDisplayHomeAsUpEnabled(true)
        val username = pref.get_str("username")
        toolbar.title = username

        val btn_add_device: Button = findViewById(R.id.btn_add_device)
        btn_add_device.setOnClickListener {
            val pref = SharedPreference(this)
            var serial_number = pref.get_int("serial_number")

            if(serial_number > 0){
                val intent = Intent(this, AddDeviceActivity::class.java)
                startActivity(intent)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_logout){
            logout()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        val pref = SharedPreference(this)
        val d_status = pref.get_str("device_status")
        btn_add.visibility = View.INVISIBLE
        if (d_status == "none"){
            val ip = pref.get_str("server_ip")
            val port = pref.get_int("server_port")
            val cntr = Controller(ip, port)
            val groups_rx = cntr.send("getworkergroups ${pref.get_str("login")}")
            val groups: ArrayList<String> = arrayListOf()
            for (g in groups_rx.split('|')){
                groups.add(g)
            }
            txt_status.text = getString(R.string.device_not_exist)
            if ("editors" in groups || "admins" in groups){
                btn_add.visibility = View.VISIBLE
            }
        } else if (d_status == "error"){
            txt_status.text = getString(R.string.something_went_wrong)
        } else {
            txt_status.text = ""
        }
        pref.set_str("device_status", "")
    }
}