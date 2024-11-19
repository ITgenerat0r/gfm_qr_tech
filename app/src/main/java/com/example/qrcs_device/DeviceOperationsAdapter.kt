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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.example.qrcs_device.objects.Data
import com.example.qrcs_device.objects.Operation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeviceOperationsAdapter(private var activity: Activity, private var items: ArrayList<Operation>, private var data: Data, private val context: Context):

    BaseAdapter() {

    val TAG = "DeviceActivity"
    private class ViewHolder(row: View){
//        lateinit var btn_edit: Button
//        var btn_delete: Button
        var txt_operation: TextView
        var txt_date: TextView
        var txt_worker: TextView
        var spin_operation: Spinner
        var btn_row: ImageView


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
        viewHolder.txt_worker.text = oper.get_name()





        val items = oper.get_operation_types()

        val arrayAdapter = ArrayAdapter<String>(
            this.activity.baseContext,
            R.layout.spinner_item,
            R.id.textView_spinner_view,
            items
        )

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

        val pos = oper.get_operation_position(oper.get_operation())
        Log.d(TAG, "Default position ${pos}")
        viewHolder.spin_operation.setSelection(pos)
        viewHolder.spin_operation.adapter = arrayAdapter








        viewHolder.btn_row.setColorFilter(context.resources.getColor(R.color.main_color))
        if (oper.get_btn_type() == "none"){
            viewHolder.btn_row.visibility = View.INVISIBLE
            viewHolder.spin_operation.visibility = View.INVISIBLE
        } else if (oper.get_btn_type() == "delete"){
            viewHolder.btn_row.setImageResource(R.drawable.baseline_clear_24)
            viewHolder.spin_operation.visibility = View.GONE
            viewHolder.txt_operation.visibility = View.VISIBLE
        }else if (oper.get_btn_type() == "add"){
//            viewHolder.btn_row.setBackgroundResource(R.drawable.baseline_add_24)
            viewHolder.btn_row.setImageResource(R.drawable.baseline_add_24)
            viewHolder.spin_operation.visibility = View.VISIBLE
            viewHolder.spin_operation.background.setTint(context.resources.getColor(R.color.main_color))
            viewHolder.txt_operation.visibility = View.GONE
        }


        val cntr = Controller(context)
        viewHolder.btn_row.setOnClickListener {
            Log.d(TAG, "Pressed row button. Position: ${position}. Button type: ${oper.get_btn_type()}.")
            if (oper.get_btn_type() == "delete"){
                Log.d(TAG, "= delete")
//                val rx = cntr.send("operation ${oper.get_btn_type()} ${data.login}|${data.serial_number}|${viewHolder.txt_operation.text}|${viewHolder.txt_date.text}")
                val rx = cntr.send("testoperation delete ${oper.get_id()}")
                Log.d(TAG, "rx: ${rx}")
                if (rx == "ok"){
                    this.items.removeAt(position)
                    this.notifyDataSetChanged()
                    Log.d(TAG, "deleted")
                }
            } else if (oper.get_btn_type() == "add"){
                Log.d(TAG, "= add")
                val rx = cntr.send("operation add ${data.login}|${data.serial_number}|${viewHolder.spin_operation.selectedItem}")
                Log.d(TAG, "rx: ${rx}")
                val rx_data = rx.split(' ')
                if (rx_data[0] == "ok"){
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val currentDate: String = sdf.format(Date())
                    this.items.get(position).set_id(rx_data[1].toInt())
                    this.items.get(position).set_btn_type("delete")
                    this.items.get(position).set_name(data.username)
                    this.items.get(position).set_operation(viewHolder.spin_operation.selectedItem.toString())
                    this.items.get(position).set_date(currentDate)


                    val operation_types: MutableList<String> = mutableListOf()
                    val op_types = cntr.send("getoperationtypes")
                    for (st in op_types.split('|')){
                        operation_types.add(st)
                    }
                    val empty_operation = Operation()
                    empty_operation.set_worker(data.login)
                    empty_operation.set_editable(true)
                    empty_operation.set_operation_types(operation_types)
                    empty_operation.set_btn_type("add")
                    this.items.add(empty_operation)

                    this.notifyDataSetChanged()
                    Log.d(TAG, "added")
                }
            }

        }






        return view as View
    }


}