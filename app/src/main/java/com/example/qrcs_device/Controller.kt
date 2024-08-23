package com.example.qrcs_device

import android.annotation.SuppressLint
import android.content.Context
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

class Controller(ip_address: String, port: Int, context: Context) {

    private val TAG = "MainActivity"
    private var server_ip = ip_address
    private var server_port = port
    private val security: Security
    var aes_key = "develop"
    var session_id = 0
    val pref: SharedPreference = SharedPreference(context)


    init {
        security = Security()
        session_id = pref.get_int("session")
        server_ip = pref.get_str("server_ip")
        server_port = pref.get_int("server_port")

//        aes.setKey(aes_key)
    }
//    private var socket: Socket = Socket(server_ip, server_port)


    fun set_ip(ip: String){
        server_ip = ip
    }

    fun set_port(port: Int){
        server_port = port
    }

//    fun reconnect(){
//        socket = Socket(server_ip, server_port)
//    }

    fun send(data_clear: String): String{
//        val data = aes.encrypt(data_clear, aes_key)
//        val r =  send_bit("e_$data").substring(2)
//        return "${aes.decrypt(r, aes_key)}"

        return send_bit("e_$data_clear").substring(2)
    }

    private fun send_bit(data: String): String{
        val queue = LinkedBlockingQueue<String>()
        val th = Thread {
            var res = "empty"
            try {
                val socket = Socket(server_ip, server_port)
                val out = PrintWriter(
                    BufferedWriter(
                        OutputStreamWriter(socket!!.getOutputStream())
                    ),
                    true
                )
                out.println(data)
                Log.d(TAG, "Sended!")

//              we can get response here
                val inputStream = socket.getInputStream()
                val buffer = ByteArray(1024)
                val bytesRead = inputStream.read(buffer)
                val response = String(buffer, 0, bytesRead)

                Log.d(TAG, String.format("Received: %s", response))
                res = response

            } catch (e: UnknownHostException) {
                e.printStackTrace()
                Log.d(TAG, String.format(" Unknown Error: %s", e.toString()))
                res = String.format(" Unknown Error: %s", e.toString())
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d(TAG, String.format("IO Error: %s", e.toString()))
                res = String.format("IO Error: %s", e.toString())
            } catch (e: Exception) {
                Log.d(TAG, String.format("Error: %s", e.toString()))
                res = String.format("Error: %s", e.toString())
                e.printStackTrace()
            }
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