package com.example.qrcs_device.objects

class Device (val serial_number: Int){
    var decimal_number: String = ""
    var name: String = ""
    var type: String = ""

    fun get_serial_number(): Int{
        return this.serial_number
    }

    fun get_decimal_number(): String{
        return this.decimal_number
    }

    fun get_name(): String{
        return this.name
    }

    fun get_type(): String{
        return this.type
    }

    fun set_decimal_number(number: String){
        this.decimal_number = number
    }

    fun set_name(nm: String){
        this.name = nm
    }

    fun set_type(tp: String){
        this.type = tp
    }
    override fun toString(): String {
        return "Device(Serial number=$serial_number, Decimal number='$decimal_number', Name='$name', Type='$type')"
    }

    fun set_field(key: String, value: String){
        if (key == "decimal_number"){
            this.decimal_number = value
        } else if (key == "d_name"){
            this.name = value
        } else if (key == "d_type"){
            this.type = value
        }
    }

}