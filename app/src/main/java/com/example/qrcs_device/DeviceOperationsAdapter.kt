package com.example.qrcs_device

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.example.qrcs_device.objects.Operation

class DeviceOperationsAdapter(private var activity: Activity, private var items: ArrayList<Operation>):

    BaseAdapter() {

    val TAG = "DeviceActivity"
    private class ViewHolder(row: View){
//        lateinit var btn_edit: Button
//        var btn_delete: Button
        var txt_operation: TextView
        var txt_date: TextView
        var txt_worker: TextView
        var spin_operation: Spinner
        var btn_row: Button


        init {
//            this.btn_edit = row.findViewById(R.id.)
//            this.btn_delete = row.findViewById(R.id.btn_op_del)
            this.txt_operation = row.findViewById(R.id.textView_op_operation)
            this.txt_date = row.findViewById(R.id.textView_op_date)
            this.txt_worker = row.findViewById(R.id.textView_op_worker)
            this.spin_operation = row.findViewById(R.id.spinner_op_operation)
            this.btn_row = row.findViewById(R.id.btn_row)

        }
    }
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        Log.d(TAG, "getItem()")
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        Log.d(TAG, "getView()")
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null){
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_item_operation, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val oper = items[position]
//        val id = oper.get_id()

        viewHolder.txt_operation.text = oper.get_operation()
        viewHolder.txt_date.text = oper.get_date()
        viewHolder.txt_worker.text = oper.get_worker()





        val items = oper.get_operation_types()

        val arrayAdapter = ArrayAdapter<String>(
            this.activity.baseContext,
            R.layout.spinner_item,
            R.id.textView_spinner_view,
            items
        )
        val pos = oper.get_operation_position(oper.get_operation())
        Log.d(TAG, "Default position ${pos}")
        viewHolder.spin_operation.setSelection(pos)
        viewHolder.spin_operation.adapter = arrayAdapter



        if (oper.is_editable()){

            viewHolder.spin_operation.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    // Handle the item selection
                    val selectedItem = parent?.getItemAtPosition(position)
                    // Do something with the selected item
                    Log.d(TAG, "Selected: ${selectedItem} (${position}).")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle no item selection
                    Log.d(TAG, "Nothing selected!")
                }
            })
        }



        if (oper.get_btn_type() == "none"){
            viewHolder.btn_row.visibility = View.INVISIBLE
            viewHolder.spin_operation.visibility = View.INVISIBLE
        } else if (oper.get_btn_type() == "delete"){
            viewHolder.btn_row.setBackgroundResource(R.drawable.baseline_clear_24)
        }else if (oper.get_btn_type() == "add"){
            viewHolder.btn_row.setBackgroundResource(R.drawable.baseline_add_24)
        }
        viewHolder.btn_row.setOnClickListener {
            Log.d(TAG, "Pressed row button. Position: ${position}. Button type: ${oper.get_btn_type()}.")
            val pref = SharedPreference(activity.baseContext)
            val login = pref.get_str("login")
            val serial_number = pref.get_str("serial_number")
            val ip = pref.get_str("server_ip")
            val port = pref.get_int("server_port")
            val cntr = Controller(ip, port)
            val rx = cntr.send("operation ${oper.get_btn_type()} ${login} ${serial_number} ${viewHolder.spin_operation.selectedItem}")
        }






        return view as View
    }


}