package com.example.qrcs_device

import android.annotation.SuppressLint
import android.os.Build
import android.os.HandlerThread
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.qrcs_device.objects.AESDemo
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket
import java.net.UnknownHostException
import java.util.concurrent.LinkedBlockingQueue

class Controller(ip_address: String, port: Int) {

    private val TAG = "MainActivity"
    private var server_ip = ip_address
    private var server_port = port
    private val security: Security

    init {
        security = Security()
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

    fun send(data: String): String{
        return send_bit("e_$data").substring(2)
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
        val aes = AESDemo
        aes.setKey("develop")
        aes.test(arrayOf())
    }
}