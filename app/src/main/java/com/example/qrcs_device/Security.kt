package com.example.qrcs_device

import android.content.Context
import android.util.Log
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Security() {
    private val TAG = "SecurityClass"
    private var aes_key: String
    private var secret_key: SecretKey
    private var ivParameterSpec: IvParameterSpec
    
    init {
        aes_key = "develop"
        secret_key = setKey(aes_key)
        ivParameterSpec = IvParameterSpec(byteArrayOf(0xc3.toByte(), 0x3b.toByte(), 0xfe.toByte(), 0xae.toByte(), 0x12.toByte(), 0x63.toByte(), 0xc9.toByte(), 0x86.toByte(), 0x33.toByte(), 0xbc.toByte(), 0x9e.toByte(), 0x66.toByte(), 0xc6.toByte(), 0xab.toByte(), 0x87.toByte(), 0x46.toByte()))

    }


    fun bytes2hexstr(data: ByteArray):String{
        var row = ""
        for (b in data){
            var bt: Int = b.toInt()
            if (bt < 0) bt += 256
            if (bt < 16) row += "0"
            row += Integer.toHexString(bt)
        }
        return row
    }

    fun hexstr2bytes(data: String):ByteArray{
        val res = ByteArray((data.length/2).toInt())
        var counter = 0
        var bt = ""
        for (c in data){
            bt += c
            if (bt.length > 1){
//                Log.d(TAG, "--$counter-- $bt, ${bt.toInt(radix = 16).toByte()}")
                res[counter] = bt.toInt(radix = 16).toByte()
                counter++
                bt = ""
            }
        }
        return res
    }

    fun hash_sha256(data: String): String{
        var key = data.toByteArray(charset("UTF-8"))
        val sha = MessageDigest.getInstance("SHA-256")
        key = sha.digest(key)
        key = Arrays.copyOf(key, 32)
        return  this.bytes2hexstr(key)
    }


    fun generateAESKey(keySize: Int = 256): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(keySize)
        Log.d(TAG, "keygenerator: ${keyGenerator}")
        return keyGenerator.generateKey()
    }


    fun setKey(myKey: String):SecretKey {
//        var sha: MessageDigest
        var key = myKey.toByteArray(charset("UTF-8"))
        val sha = MessageDigest.getInstance("SHA-256")
        key = sha.digest(key)
        key = Arrays.copyOf(key, 32)
        val secretKey = SecretKeySpec(key, "AES")
        return secretKey
    }

    fun set_hashkey(new_key: String){
        this.secret_key = SecretKeySpec(this.hexstr2bytes(new_key), "AES")
    }
    

    fun set_iv(new_iv: String){
        this.ivParameterSpec = IvParameterSpec(this.hexstr2bytes(new_iv))
    }


    fun get_iv(): String{
        return this.bytes2hexstr(this.ivParameterSpec.iv)
    }


    fun show_bytes(data: ByteArray){
        Log.d(TAG, "DDDDD: $data")
        for(i in data){
            Log.d(TAG, "> $i         ${i+128}")
        }
    }



    fun aesEncrypt(data: String): String {
        Log.d(TAG, "ENCRYPT")
        Log.d(TAG, "data: $data")
        val b_data = data.toByteArray(charset("UTF-8"))
        Log.d(TAG, "b_data: ${bytes2hexstr(b_data)}")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
//        val ivParameterSpec = IvParameterSpec(ByteArray(16)) // Use a secure IV in production
//        val ivParameterSpec = IvParameterSpec(byteArrayOf(0xc3.toByte(), 0x3b.toByte(), 0xfe.toByte(), 0xae.toByte(), 0x12.toByte(), 0x63.toByte(), 0xc9.toByte(), 0x86.toByte(), 0x33.toByte(), 0xbc.toByte(), 0x9e.toByte(), 0x66.toByte(), 0xc6.toByte(), 0xab.toByte(), 0x87.toByte(), 0x46.toByte()))
        Log.d(TAG,"iv ${get_iv()}")
        Log.d(TAG, "secretKey ${bytes2hexstr(this.secret_key.encoded)}")
        cipher.init(Cipher.ENCRYPT_MODE, this.secret_key, this.ivParameterSpec)
        val dd = cipher.doFinal(b_data)
//        show_bytes(dd)
        val res = this.bytes2hexstr(dd)
        Log.d(TAG, "ENCRYPTED: $res")
        return res
    }

    fun aesDecrypt(data: String): String {
        Log.d(TAG, "DECRYPT")
        Log.d(TAG, "Encrypted data: $data")
        Log.d(TAG,"iv ${get_iv()}")
        Log.d(TAG, "secretKey ${bytes2hexstr(this.secret_key.encoded)}")
        val encryptedData = this.hexstr2bytes(data)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
//        val ivParameterSpec = IvParameterSpec(byteArrayOf(0xc3.toByte(), 0x3b.toByte(), 0xfe.toByte(), 0xae.toByte(), 0x12.toByte(), 0x63.toByte(), 0xc9.toByte(), 0x86.toByte(), 0x33.toByte(), 0xbc.toByte(), 0x9e.toByte(), 0x66.toByte(), 0xc6.toByte(), 0xab.toByte(), 0x87.toByte(), 0x46.toByte())) // Use the same IV as used in encryption
        cipher.init(Cipher.DECRYPT_MODE, this.secret_key, this.ivParameterSpec)
        val b_res = cipher.doFinal(encryptedData)
        Log.d(TAG, "Decrypted: ${this.bytes2hexstr(b_res)}")
        val res = String(b_res, charset("UTF-8"))
        Log.d(TAG, "Data: $res")
        return res
    }


    fun test() { // debug
        Log.d(TAG, "--------- TESTING --------------------------------")
        val originalText = "asdfasdf-0123456789abcef0123456789abcdef"
//        val secretKey = generateAESKey(128)
        val secretKey = setKey("develop")
        Log.d(TAG, "key ${secretKey}")

        val encryptedData = aesEncrypt(originalText)
        val str_en_data = encryptedData
        Log.d(TAG, "Encrypted data:  ${str_en_data}")
        val bytes_en_data = str_en_data
        Log.d(TAG, "Encrypted data2: ${bytes_en_data}")
//        val decrbytes_en_datayptedData = aesDecrypt(encryptedData, secretKey)
        val decryptedText = aesDecrypt(bytes_en_data)

        Log.d(TAG, "Original text: ${originalText}")
        Log.d(TAG, "Encrypted text: ${encryptedData}")
        Log.d(TAG, "Decrypted text: ${decryptedText}")
        Log.d(TAG, "--------------------------------------------------")
    }




}

