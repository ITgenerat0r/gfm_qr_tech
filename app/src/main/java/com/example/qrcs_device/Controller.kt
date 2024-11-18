package com.example.qrcs_device

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.HandlerThread
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException
import java.util.concurrent.LinkedBlockingQueue

class Controller(private val context: Context) {

    private val TAG = "SecurityClass"
    private var server_ip = ""
    private var server_port = 11200
    private val security: Security
    private var aes_key = "develop"
    private var session_id = 0
    private val pref: SharedPreference
    private var iv = ""
    private var encryption_enabled = false


    init {
        Log.d(TAG, "INIT")
        Log.d(TAG, "Security...")
        security = Security()
        Log.d(TAG, "SharedPreferences...")
        pref  = SharedPreference(context)
        Log.d(TAG, "Get strings...")
        session_id = pref.get_int("session")
        server_ip = pref.get_str("server_ip")
        server_port = pref.get_int("server_port")

        Log.d(TAG, "Set session...")
        if (session_id == 0){
            val rx = send("ns")
            Log.d(TAG, "rx: $rx")
            val data_rx = rx.split(' ')
            if (data_rx.size > 1){
                session_id = data_rx[0].toInt()
                iv = data_rx[1]
                pref.set_str("iv", iv)
                enable_encryption()
            }

        }
        Log.d(TAG, "INIT Done")
        Log.d(TAG, "Internet access: ${checkForInternet()}")

//        aes.setKey(aes_key)
    }
//    private var socket: Socket = Socket(server_ip, server_port)


    fun checkForInternet(): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = this.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection

        // Returns a Network object corresponding to
        // the currently active default data network.
        val network = connectivityManager.activeNetwork ?: return false

        // Representation of the capabilities of an active network.
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            // Indicates this network uses a Wi-Fi transport,
            // or WiFi has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

            // Indicates this network uses a Cellular transport. or
            // Cellular has network connectivity
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

            // else return false
            else -> false
        }
    }


    fun enable_encryption(s: Boolean = true){
        this.encryption_enabled = s
    }
    fun set_ip(ip: String){
        server_ip = ip
    }

    fun set_port(port: Int){
        server_port = port
    }

//    fun reconnect(){
//        socket = Socket(server_ip, server_port)
//    }

    fun send(data: String): String{
//        val data = aes.encrypt(data_clear, aes_key)
//        val r =  send_bit("e_$data").substring(2)
//        return "${aes.decrypt(r, aes_key)}"

        var sdata = data
        if (encryption_enabled){
            val iv = pref.get_str("iv")
            security.set_iv(iv)
            sdata = security.aesEncrypt(data)
//            pref.set_str("iv", sdata)
        }
        var session_str = ""
        if (session_id > 0){
            session_str = "$session_id "
        }
        val rx = send_bit("e_$session_str$sdata").substring(2)

        if (encryption_enabled){
            security.set_iv(sdata.substring(sdata.length-32))
            val rdata = security.aesDecrypt(rx)
            pref.set_str("iv", rx.substring(rx.length-32))
            return rdata
        }

        return rx
    }

    private fun send_bit(data: String): String{
        val queue = LinkedBlockingQueue<String>()
        val th = Thread {
            var res = "empty"
            var err = ""
            try {
                val socket = Socket(server_ip, server_port)
                val out = PrintWriter(
                    BufferedWriter(
                        OutputStreamWriter(socket.getOutputStream())
                    ),
                    true
                )
                out.println(data)
                Log.d(TAG, "Sended: $data")

//              we can get response here
                val inputStream = socket.getInputStream()
                val buffer = ByteArray(1024)
                val bytesRead = inputStream.read(buffer)
                val response = String(buffer, 0, bytesRead)

                Log.d(TAG, "Received: $response")
                res = response

            } catch (e: UnknownHostException) {
                e.printStackTrace()
                err = e.toString()
                Log.d(TAG, String.format(" Unknown Error: %s", e.toString()))
                res = String.format(" Unknown Error: %s", e.toString())
            } catch (e: IOException) {
                e.printStackTrace()
                err = e.toString()
                Log.d(TAG, String.format("IO Error: %s", e.toString()))
                res = String.format("IO Error: %s", e.toString())
            } catch (e: Exception) {
                err = e.toString()
                Log.d(TAG, String.format("Error: %s", e.toString()))
                res = String.format("Error: %s", e.toString())
                e.printStackTrace()
            }
            Log.d(TAG, "Connect error: $err")
            queue.add(res)
        }
        th.start()
        th.join()
        val rrr: String = queue.take()
        Log.d(TAG, "Received bit: $rrr")
        return rrr
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun test(){
//        security.test() // debug


//        val aes = AESDemo
//        aes.setKey("develop")
//        aes.test(arrayOf())
    }
}