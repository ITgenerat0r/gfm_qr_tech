package com.example.qrcs_device

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar

class AddDeviceActivity : AppCompatActivity() {
    val TAG = "AddDeviceActivity"

    lateinit var input_decimal: EditText
    lateinit var input_name: EditText
    lateinit var input_type: EditText
    lateinit var btn_apply: Button
    lateinit var ip: String
    lateinit var cntr: Controller
    lateinit var pref: SharedPreference
    lateinit var login: String


    var added = false
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)


        input_decimal = findViewById(R.id.edit_decimal_number)
        input_name = findViewById(R.id.edit_name)
        input_type = findViewById(R.id.edit_type)
        btn_apply = findViewById(R.id.btn_add_device_apply)
        window.statusBarColor = resources.getColor(R.color.black)


        Log.d(TAG, "pref init...")
        pref = SharedPreference(this)
        ip = pref.get_str("server_ip")
        val port = pref.get_int("server_port")
        cntr = Controller(this)
        login = pref.get_str("login")
        val act = pref.get_str("add_action")

        Log.d(TAG, "first stage done")

        val toolbar: Toolbar = findViewById(R.id.toolbar_add_device)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        Log.d(TAG, "toolbar initial")

//        login = pref.get_str("login")

        val serial_number = pref.get_str("serial_number_str")
        Log.d(TAG, "serial_number: $serial_number")
        val decimal_number = pref.get_str("decimal")
        Log.d(TAG, "decimal_number: $decimal_number")
        val device_name = pref.get_str("device_name")
        Log.d(TAG, "device_name: $device_name")

        Log.d(TAG, "second stage done")

        toolbar.title = serial_number
        toolbar.setTitleTextColor(resources.getColor(R.color.black))
        toolbar.setBackgroundColor(resources.getColor(R.color.main_color))

        Log.d(TAG, "toolbar done")

        input_decimal.setText(decimal_number)
        input_name.setText(device_name)

        Log.d(TAG, "init onClickListener()")

        btn_apply.setBackgroundColor(resources.getColor(R.color.main_color))
        btn_apply.setTextColor(resources.getColor(R.color.main_text))
        btn_apply.setOnClickListener {
            var cmd = ""
            if (act == "add"){
                cmd = "createdevice"
            } else if (act == "edit"){
                cmd = "updatedevice"
            }
            var tx: String = "$cmd ${login} serial:${serial_number}"
            if(input_decimal.text.toString() != ""){
                tx += "|decimal:${input_decimal.text}"
            }
            if(input_name.text.toString() != ""){
                tx += "|name:${input_name.text}"
            }
//            if(input_type.text.toString() != ""){
//                tx += "|type:${input_type.text}"
//            }
            val rx = cntr.send(tx)
            Log.d(TAG, rx)
            pref.set_int("serial_number", serial_number.toInt())
            added = true
            val intent = Intent(this, DeviceActivity::class.java)
            startActivity(intent)
        }

        Log.d(TAG, "onCreate() END")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.device_menu, menu)
        val menuitem_del = menu?.findItem(R.id.id_delete_device)
        menuitem_del?.setVisible(false)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
//        val pref = SharedPreference(this)
        pref.set_str("device_status", "")
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        if(added){
            finish()
        }
    }
}