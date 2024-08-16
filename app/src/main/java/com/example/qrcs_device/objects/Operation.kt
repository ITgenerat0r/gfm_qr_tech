package com.example.qrcs_device.objects

class Operation() {
    var operation: String = ""
    var operation_types = mutableListOf<String>()
    var worker: String = ""
    var date: String = ""
    var editable: Boolean = false
    var btn_type: String = "none"



    fun get_operation_types(): MutableList<String>{
        return this.operation_types
    }

    fun get_operation_position(type: String): Int{
        var count = 0
        for (tp in this.operation_types){
            if (tp == type) return count
            count += 1
        }
        return 0
    }

    fun set_operation_types(items: MutableList<String>){
        this.operation_types = items
    }

    fun get_operation(): String{
        return this.operation
    }

    fun set_operation(op: String){
        this.operation = op
    }


    fun get_worker(): String{
        return this.worker
    }

    fun set_worker(w: String){
        this.worker = w
    }


    fun get_date(): String{
        return this.date
    }

    fun set_date(dt: String){
        this.date = dt
    }

    fun get_btn_type(): String{
        return this.btn_type
    }

    fun set_btn_type(tp: String){
        this.btn_type = tp
    }

    fun is_editable():Boolean{
        return this.editable
    }

    fun set_editable(e: Boolean){
        this.editable = e
    }

    fun get_field(key: String): String{
        if (key == "date") return this.date
        if (key == "worker") return this.worker
        if (key == "operation") return this.operation
        return ""
    }

    fun set_field(key: String, value: String){
        if (key == "date"){
            this.date = value
        } else if (key == "worker"){
            this.worker = value
        } else if (key == "operation"){
            this.operation = value
        }
    }

    override fun toString(): String {
        return "Operation(operation='$operation', worker='$worker', date='$date')"
    }




}