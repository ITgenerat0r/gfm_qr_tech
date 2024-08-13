package com.example.qrcs_device

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.example.qrcs_device.objects.Device
import com.example.qrcs_device.objects.Operation

class DeviceActivity : AppCompatActivity() {
    val TAG = "DeviceActivity"

    var operations: ArrayList<Operation> = arrayListOf()
    lateinit var listview_operations: ListView
//    var operations: MutableMap<String, String> = mutableMapOf()
//    lateinit var login: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)


        val toolbar: Toolbar = findViewById(R.id.toolbar_device)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        val pref = SharedPreference(this)
//        login = pref.get_str("login")
        val serial_number = pref.get_int("serial_number")
        toolbar.title = serial_number.toString()

        val text_device = findViewById<TextView>(R.id.text_device_data)
        listview_operations = findViewById(R.id.listview_operations)



        val ip = pref.get_str("server_ip")
        val port = pref.get_int("server_port")
        val cntr = Controller(ip, port)
        // get user groups here ...
        val groups_rx = cntr.send("getworkergroups ${pref.get_str("login")}")
        val groups: ArrayList<String> = arrayListOf()
        for (g in groups_rx.split('|')){
            groups.add(g)
        }
        if ("workers" in groups){
            Log.d(TAG, "WORKER")
        }

        // ========== DEVICE ==================================================
        val rx = cntr.send("getdevicedata ${serial_number}")
        Log.d(TAG, rx)
        if (rx == "none" || rx == "error"){
            Log.d(TAG, "EXIT()")
            pref.set_str("device_status", rx)
            finish()
            return
        }
        val device = Device(serial_number)
        for (i in rx.split('|')){
            val row = i.split(':')
            device.set_field(row[0], row[1])
            Log.d(TAG, "Row: ${row[0]}, ${row[1]}.")
        }
        text_device.text = getString(R.string.serial_number) + ": " + device.get_serial_number()
        text_device.text = "${text_device.text}\n${getString(R.string.decimal_number)}: МКЦБ." + device.get_decimal_number()
        text_device.text = "${text_device.text}\n${getString(R.string.device_name)}: ${device.get_name()}"
        text_device.text = "${text_device.text}\n${getString(R.string.device_type)}: ${device.get_type()}"
        // ====================================================================

        // ========== OPERATIONS ==============================================
        val oper_data = cntr.send("getdeviceoperations ${serial_number}")
        Log.d(TAG, "oper_data")
        Log.d(TAG, oper_data)
        if (oper_data != "none" && oper_data != "error"){
            val title_op = Operation()
            title_op.set_date(getString(R.string.date))
            title_op.set_worker(getString(R.string.worker))
            title_op.set_operation(getString(R.string.operation))
//            title_op.set_operation("Operation")
//            title_op.set_worker("Worker")
            operations.add(title_op)
            for(op in oper_data.split('•')){
                val operation = Operation()
                for (row in op.split('|')){
                    val key = row.split(':')
                    Log.d(TAG, "${key[0]}: ${key[1]}")
                    operation.set_field(key[0], key[1])
                }
                Log.d(TAG, "=====================")
                Log.d(TAG, operation.toString())
                Log.d(TAG, "=====================")
                operations.add(operation)
            }
        }
        // log
        Log.d(TAG, "count operations: ${operations.size}")

        // ====================================================================

        val adapter = DeviceOperationsAdapter(this, operations)
        listview_operations.adapter = adapter
        adapter.notifyDataSetChanged()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.device_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val pref = SharedPreference(this)
        pref.set_str("device_status", "")
        onBackPressed()
        return true
    }
}