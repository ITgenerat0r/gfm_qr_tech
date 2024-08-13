package com.example.qrcs_device.objects

class Operation() {
    var operation: String = ""
    var worker: String = ""
    var date: String = ""


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