package com.example.qrcs_device

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar



class ChooseInput : AppCompatActivity() {

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


        val btn_qr = findViewById<Button>(R.id.btn_read_qr)
        btn_qr.setOnClickListener {
            val pref = SharedPreference(this)
            pref.set_str("qr_code", "")
            // run QR reader here

            //
            val qr_data = pref.get_str("qr_code") // need handler or in onResume()
        }

        val btn_apply = findViewById<Button>(R.id.btn_apply)
        btn_apply.setOnClickListener {
            val input_number = findViewById<EditText>(R.id.input_serial_number)
            val serial_number = input_number.text
            Log.d(TAG, "Number: ${serial_number}")

            // get data for this number and user then run edit or readonly activity
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar_choose_input)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
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
}