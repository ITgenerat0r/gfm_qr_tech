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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.google.zxing.integration.android.IntentIntegrator


class ChooseInput : AppCompatActivity() {
    private val TAG = "ChooseInput"
    lateinit var txt_status: TextView
    lateinit var btn_add: Button
    lateinit var input_number: TextView
    lateinit var txt_version: TextView



    fun logout(){
        val preferences = SharedPreference(this)
        preferences.set_str("passwd", "")
        preferences.set_str("username", "")
        val intent = Intent(this, Auth::class.java)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_input)

        input_number = findViewById<EditText>(R.id.input_serial_number)

        txt_status = findViewById(R.id.textView_status)
        txt_version = findViewById(R.id.textView_version)
        btn_add = findViewById(R.id.btn_add_device)
        btn_add.visibility = View.INVISIBLE


        window.statusBarColor = resources.getColor(R.color.black)


        val btn_qr = findViewById<Button>(R.id.btn_read_qr)
        btn_qr.setBackgroundColor(resources.getColor(R.color.main_color))
        btn_qr.setTextColor(resources.getColor(R.color.main_text))
        btn_qr.setOnClickListener {
            val pref = SharedPreference(this)
//            pref.set_str("qr_code", "")
            // run QR reader here

            val intent = Intent(this, readQR::class.java)
            startActivity(intent)
            //
//            val qr_data = pref.get_str("qr_code") // need handler or in onResume()
            // set qr_data to input field

        }
        val pref = SharedPreference(this)
        val btn_apply = findViewById<Button>(R.id.btn_apply)
        btn_apply.setBackgroundColor(resources.getColor(R.color.main_color))
        btn_apply.setTextColor(resources.getColor(R.color.main_text))
        btn_apply.setOnClickListener {
            var serial_number = 0

            if(input_number.text.toString() != ""){
                serial_number = input_number.text.toString().toInt()


                pref.set_int("serial_number", serial_number)
                val intent = Intent(this, DeviceActivity::class.java)
                startActivity(intent)
            }

            Log.d(TAG, "Number: ${serial_number}")

        }

        txt_version.text = "${getString(R.string.version)} ${pref.get_str("version")}"
        val toolbar: Toolbar = findViewById(R.id.toolbar_choose_input)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
//        actionBar?.setDisplayHomeAsUpEnabled(true)
        val username = pref.get_str("username")
        toolbar.title = username
        toolbar.setTitleTextColor(resources.getColor(R.color.black))
        toolbar.setBackgroundColor(resources.getColor(R.color.main_color))

        val btn_add_device: Button = findViewById(R.id.btn_add_device)
        btn_add_device.setBackgroundColor(resources.getColor(R.color.main_color))
        btn_add_device.setTextColor(resources.getColor(R.color.main_text))
        btn_add_device.setOnClickListener {
            val pref = SharedPreference(this)
            var serial_number = pref.get_int("serial_number")
            pref.set_str("add_action", "add")

            if(serial_number > 0){
                val intent = Intent(this, AddDeviceActivity::class.java)
                startActivity(intent)
            }
        }

        val btn_decrease = findViewById<ImageView>(R.id.btn_decrease)
        val btn_increase = findViewById<ImageView>(R.id.btn_increase)
        btn_decrease.setColorFilter(resources.getColor(R.color.main_color))
        btn_increase.setColorFilter(resources.getColor(R.color.main_color))
        btn_decrease.setOnClickListener {
            Log.d(TAG, "decrease")
            serial_number_add(-1)
        }
        btn_increase.setOnClickListener {
            Log.d(TAG, "increase")
            serial_number_add(1)
        }

    }

    private fun serial_number_add(k: Int){
        var sn = input_number.text.toString()
        Log.d(TAG, "number: $sn")
        if (sn != ""){
            Log.d(TAG, "not empty")
            try {
                val nn = sn.toInt() + k
                Log.d(TAG, "new number: $nn")
                if (nn > 0){
                    input_number.setText("$nn")
                    val pref = SharedPreference(this)
                    pref.set_int("serial_number", nn)
                }
                Log.d(TAG, "done.")
            } catch (e: Exception){
                Log.d(TAG, "wrong number")
            }
        }
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult()")

        val res = IntentIntegrator.parseActivityResult(resultCode, data)
        Log.d(TAG, "res: $res")
        if (res != null){
            if (res.contents == null){
                Toast.makeText(baseContext, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "\"res: ${res.contents} | ${res.formatName}\"")
                input_number.setText(res.contents)

                val intentIntegrator = IntentIntegrator(this)
                intentIntegrator.setBeepEnabled(false)
                intentIntegrator.setPrompt("Scan a barcode or QR code")
                intentIntegrator.setOrientationLocked(false)
                intentIntegrator.setBarcodeImageEnabled(false)
                intentIntegrator.setCaptureActivity(readQR::class.java)
                intentIntegrator.initiateScan()
            }
        }
        Log.d(TAG, "END")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_logout){
            logout()
        } else if (item.itemId == R.id.id_settings){
            // run settings activity
            val intent = Intent(this, Settings::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
        val pref = SharedPreference(this)
        val d_status = pref.get_str("device_status")
        btn_add.visibility = View.INVISIBLE
        btn_add.setTextColor(resources.getColor(R.color.main_text))
        if (d_status == "none"){
            val ip = pref.get_str("server_ip")
            val port = pref.get_int("server_port")
            val cntr = Controller(this)
            val groups_rx = cntr.send("getworkergroups ${pref.get_str("login")}")
            val groups: ArrayList<String> = arrayListOf()
            for (g in groups_rx.split('|')){
                groups.add(g)
            }
            txt_status.text = getString(R.string.device_not_exist)
            if ("editors" in groups || "admins" in groups || "workers" in groups){
                btn_add.visibility = View.VISIBLE
            }
        } else if (d_status == "error"){
            txt_status.text = getString(R.string.something_went_wrong)
        } else {
            txt_status.text = ""
        }
        val sn = pref.get_str("QR_result")
        val sn_data = sn.split("|")
        Log.d(TAG, "QR result: $sn")
        if (sn != ""){
            Log.d(TAG, "qr not empty")
            val intxt = findViewById<EditText>(R.id.input_serial_number)
            try {
                pref.set_int("serial_number", sn_data[0].toInt())
            } catch (e: Exception){
                Log.d(TAG, "wrong serial number")
            }


            intxt.setText("${sn_data[0]}")
        }
//        pref.set_str("QR_result", "")
        pref.set_str("device_status", "")
        if (pref.get_bool("restartQR")){
            val intent = Intent(this, readQR::class.java)
            startActivity(intent)
        }
        Log.d(TAG, "END")
    }
}