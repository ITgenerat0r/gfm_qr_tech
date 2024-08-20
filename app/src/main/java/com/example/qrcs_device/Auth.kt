package com.example.qrcs_device

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class Auth : AppCompatActivity() {

    val TAG = "Auth";




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val pref = SharedPreference(this)
        val login = pref.get_str("login")
        val login_input: EditText = findViewById(R.id.input_login)
        if (login != ""){
            login_input.setText(login)
        }
        val passwd_input: EditText = findViewById(R.id.input_password)
        val signin_btn: Button = findViewById(R.id.btn_signin)
        val out_errors: TextView = findViewById(R.id.textView_errors)

        signin_btn.setOnClickListener {
            val ip = pref.get_str("server_ip")
            val port = pref.get_int("server_port")
            val cntr = Controller(ip, port)
            cntr.test() // debug
            val login_i = login_input.text
            val passwd = passwd_input.text
            // sign in here
            val rq = "lg ${login_i} ${passwd}"
            Log.d(TAG, String.format("tx: %s", rq))
            val rx = cntr.send(rq)
            Log.d(TAG, String.format("rx: %s", rx))


            pref.set_str("login", login_i.toString())
            pref.set_str("passwd", passwd.toString())

            val rx_data = rx.split(' ')
            if (rx_data.size > 1){
                pref.set_str("username", rx_data[1])
            }

            if (rx_data.isNotEmpty() && rx_data[0] == "success"){
                pref.set_str("action", "auth")
                val intent = Intent(this, ChooseInput::class.java)
                startActivity(intent)
//                finish()
            } else {
                out_errors.setText(R.string.wrong_login)
            }

        }
    }
}