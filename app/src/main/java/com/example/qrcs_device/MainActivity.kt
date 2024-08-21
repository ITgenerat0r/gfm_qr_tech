package com.example.qrcs_device

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar


class MainActivity : AppCompatActivity() {

    val version = "1.0"

    var ip = "192.168.47.252"
    var port = 11200
    val cntr = Controller(ip, port)
    var aes_key = "develop"


    val TAG = "MainActivity"

    var log_view: TextView? = null
    var command_input: EditText? = null
    var send_btn: Button? = null
    var toolbar: Toolbar? = null



    private val preferences = SharedPreference(this);

//    fun checkStoragePermission(): Boolean {
//        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED)
//            return false
//        }
//        return true
//    }
    fun logout(){
        preferences.set_str("passwd", "")
        preferences.set_str("username", "")
        val intent = Intent(this, Auth::class.java)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences.set_str("version", version)
        preferences.set_str("server_ip", ip)
        preferences.set_int("server_port", port)

        Log.d(TAG, "onCreate()")
        val login = preferences.get_str("login");
        val password = preferences.get_str("passwd");
        val rq = "lg ${login} ${password}"
        Log.d(TAG, String.format("tx: %s", rq))
        val rx = cntr.send(rq)
        Log.d(TAG, String.format("rx: %s", rx))
        if (login == "" || password == "" || rx == "error"){
            val intent = Intent(this, Auth::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, ChooseInput::class.java)
            startActivity(intent)
        }

        log_view = findViewById(R.id.textView_log)
        command_input = findViewById(R.id.EditText_command)
        send_btn = findViewById(R.id.button_send)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)

//        val username = preferences.get_str("username")
//        if (username.isNotEmpty()){
//            log_view!!.setText("${log_view!!.text.toString()} \nLogged as ${username}")
//        }

        send_btn?.setOnClickListener {
            val command = command_input?.editableText.toString()
            if (command == "logout"){
                logout()
                return@setOnClickListener
            }
            log_view!!.text = "${log_view!!.text.toString()}\n => $command"
            Log.d(TAG, String.format("Command: %s", command))
            val response = cntr.send(command)
//            Log.d(TAG, "Sended!")
//            val  response = cntr.recv()
            log_view!!.text = "${log_view!!.text.toString()}\r\n <= $response"
            Log.d(TAG, "Done!")

        }
    }

    override fun onResume() {
        super.onResume()
//        check if not developer
        logout()
        if (preferences.get_str("action") == "auth"){
            val username = preferences.get_str("username")
            if (username.isNotEmpty()){
                log_view!!.setText("${log_view!!.text.toString()} \nLogged as ${username}")
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
}