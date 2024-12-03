package com.example.qrcs_device

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

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

        window.statusBarColor = resources.getColor(R.color.black)

        signin_btn.setBackgroundColor(resources.getColor(R.color.main_color))
        signin_btn.setTextColor(resources.getColor(R.color.main_text))
        signin_btn.setOnClickListener {
            val cntr = Controller(this)
            if (!cntr.checkForInternet()){
                out_errors.text = getString(R.string.check_internet)
            }
//            cntr.set_context(this)
            val login_i = login_input.text
            val ss = Security()
            val hash = ss.hash_sha256(passwd_input.text.toString())
            // sign in here
            val rq = "lg ${login_i} ${hash}"
            Log.d(TAG, String.format("tx: %s", rq))
            val rx = cntr.send(rq)
            Log.d(TAG, String.format("rx: %s", rx))


            pref.set_str("login", login_i.toString())
            pref.set_str("passwd", hash)

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