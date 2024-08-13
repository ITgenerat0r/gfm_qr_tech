package com.example.qrcs_device

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
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

        init {
//            this.btn_edit = row.findViewById(R.id.)
//            this.btn_delete = row.findViewById(R.id.btn_op_del)
            this.txt_operation = row.findViewById(R.id.textView_op_operation)
            this.txt_date = row.findViewById(R.id.textView_op_date)
            this.txt_worker = row.findViewById(R.id.textView_op_worker)
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

//        viewHolder.btn_delete.setOnClickListener {
//            Log.d(TAG, "onClick(Delete $position)")
////            val storage = Storage(activity.baseContext)
////            storage.load()
////            storage.delete_device(id)
////            storage.save()
////            items = storage.for_adapter()
//            this.notifyDataSetChanged()
//        }


        return view as View
    }


}