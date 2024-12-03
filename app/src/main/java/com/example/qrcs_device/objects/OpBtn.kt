package com.example.qrcs_device.objects

import android.content.Context
import com.example.qrcs_device.SharedPreference

class OpBtn (private val op: String){
    private var me = false
    private var others = mutableSetOf<String>()
    private var ids = mutableSetOf<Int>()

    fun get_op():String{
        return op
    }

    fun get_me():Boolean{
        return me
    }

    fun get_others_count(): Int{
        return others.size
    }

    fun switch_me(){
        me = !me
    }

    fun add_id(i: Int){
        ids.add(i)
    }

    fun get_ids(): MutableSet<Int>{
        return ids
    }

    fun delete_id(i: Int){
        ids.remove(i)
    }



    fun add(cnt: Context, oper: Operation){
        val pref = SharedPreference(cnt)
        val login = pref.get_str("login")
        if (oper.get_operation() == op){
            if (oper.get_worker() == login){
                me = true
                ids.add(oper.get_id())
            } else {
                others.add(oper.get_worker())
            }
        }
    }
}