package com.example.qrcs_device.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import com.example.qrcs_device.Controller
import com.example.qrcs_device.R
import com.example.qrcs_device.SharedPreference
import com.example.qrcs_device.objects.OpBtn
import com.example.qrcs_device.objects.Operation


class ButtonsAdapter(private var activity: Activity, private var items: ArrayList<OpBtn>, private val login: String = "", private val serial_number: String = ""):

    BaseAdapter() {

    val TAG = "ButtonsAdapter"

    private class ViewHolder(row: View, cnt: Context) {
        var btn: Button
        var img_state: ImageView
        val context: Context

        init {
            this.btn = row.findViewById(R.id.btn_op)
            this.img_state = row.findViewById(R.id.img_state)
            this.context = cnt

            this.btn.setBackgroundColor(context.resources.getColor(R.color.main_color))
            change_img(false, 0)
        }

        fun change_img(me: Boolean, others: Int){
            if (me){
                if (others ==  0){
                    this.img_state.setImageResource(R.drawable.baseline_done_24)
                    this.img_state.setColorFilter(context.getColor(R.color.green))
                } else if (others > 0){
                    this.img_state.setImageResource(R.drawable.baseline_done_all_24)
                    this.img_state.setColorFilter(context.getColor(R.color.green))
                }
            } else {
                if (others ==  0){
                    this.img_state.setImageResource(R.drawable.baseline_clear_24)
                    this.img_state.setColorFilter(context.getColor(R.color.red))
                } else if (others == 1){
                    this.img_state.setImageResource(R.drawable.baseline_done_24)
                    this.img_state.setColorFilter(context.getColor(R.color.yellow))
                } else if (others > 1){
                    this.img_state.setImageResource(R.drawable.baseline_done_all_24)
                    this.img_state.setColorFilter(context.getColor(R.color.yellow))
                }
            }
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
        Log.d(TAG, "getPartView($position)")
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater =
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.item_button, null)
            viewHolder = ViewHolder(view, activity.baseContext)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        Log.d(TAG, "init share...")
//        val pref = SharedPreference(cnt)
//        val login = pref.get_str("login")
//        val serial_number = pref.get_str("serial_number")

        Log.d(TAG, "init btn...")

        viewHolder.btn.setText(items[position].get_op())
        viewHolder.change_img(items[position].get_me(), items[position].get_others_count())

        Log.d(TAG, "init onClickListener...")
        viewHolder.btn.setOnClickListener {
//            Toast.makeText(activity.baseContext, "$position: ${viewHolder.btn.text}", Toast.LENGTH_SHORT).show()

            val cntr = Controller(activity.baseContext)
            var rx = ""
            if (items[position].get_me()){
                var com_rx = "ok"
                val r = mutableMapOf<Int, String>()
                for (i in items[position].get_ids()){
                    Log.d(TAG, "operation delete $i")
                    rx = cntr.send("operation delete $i")
                    Log.d(TAG, "rx: $rx")
                    if (rx != "ok"){
                        com_rx = rx
                        Toast.makeText(activity.baseContext, "Can't delete: ${viewHolder.btn.text} \n Error: $rx", Toast.LENGTH_SHORT).show()
                    }
                    r[i] = rx

                }
                for ((k, v) in r){
                    items[position].delete_id(k)
                }
                if (com_rx == "ok"){
                    items[position].switch_me()
                    viewHolder.change_img(items[position].get_me(), items[position].get_others_count())
                } else {
                    Log.d(TAG, "com_rx: $com_rx")
                }



            } else {
                Log.d(TAG, "operation add ${login}|${serial_number}|${items[position].get_op()}")
                rx = cntr.send("operation add ${login}|${serial_number}|${items[position].get_op()}")
                Log.d(TAG, "rx: $rx")
                val rx_data = rx.split(" ")
                if (rx_data[0] == "ok"){
                    items[position].switch_me()
                    items[position].add_id(rx_data[1].toInt())
                    viewHolder.change_img(items[position].get_me(), items[position].get_others_count())
                } else {
                    Toast.makeText(activity.baseContext, "Can't add operation: ${viewHolder.btn.text} \n Error: $rx", Toast.LENGTH_SHORT).show()
                }
            }



        }
        Log.d(TAG, "done.")




        return view as View
    }

}