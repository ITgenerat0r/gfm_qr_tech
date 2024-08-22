package com.example.qrcs_device

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

class Security {
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
        val res = ByteArray(16)
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

    fun set_iv(new_iv: String){
        this.ivParameterSpec = IvParameterSpec(this.hexstr2bytes(new_iv))
    }


    fun get_iv(): String{
        return this.bytes2hexstr(this.ivParameterSpec.iv)
    }



    fun aesEncrypt(data: ByteArray, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
//        val ivParameterSpec = IvParameterSpec(ByteArray(16)) // Use a secure IV in production
//        val ivParameterSpec = IvParameterSpec(byteArrayOf(0xc3.toByte(), 0x3b.toByte(), 0xfe.toByte(), 0xae.toByte(), 0x12.toByte(), 0x63.toByte(), 0xc9.toByte(), 0x86.toByte(), 0x33.toByte(), 0xbc.toByte(), 0x9e.toByte(), 0x66.toByte(), 0xc6.toByte(), 0xab.toByte(), 0x87.toByte(), 0x46.toByte()))
        Log.d(TAG,"iv ${bytes2hexstr(this.ivParameterSpec.iv)}")
        Log.d(TAG, "secretKey ${bytes2hexstr(secretKey.encoded)}")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, this.ivParameterSpec)
        return cipher.doFinal(data)
    }

    fun aesDecrypt(encryptedData: ByteArray, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
//        val ivParameterSpec = IvParameterSpec(byteArrayOf(0xc3.toByte(), 0x3b.toByte(), 0xfe.toByte(), 0xae.toByte(), 0x12.toByte(), 0x63.toByte(), 0xc9.toByte(), 0x86.toByte(), 0x33.toByte(), 0xbc.toByte(), 0x9e.toByte(), 0x66.toByte(), 0xc6.toByte(), 0xab.toByte(), 0x87.toByte(), 0x46.toByte())) // Use the same IV as used in encryption
        cipher.init(Cipher.DECRYPT_MODE, secretKey, this.ivParameterSpec)
        return cipher.doFinal(encryptedData)
    }


    fun test() { // debug
        val originalText = "asdfasdf"
//        val secretKey = generateAESKey(128)
        val secretKey = setKey("develop")
        Log.d(TAG, "key ${secretKey}")

        val encryptedData = aesEncrypt(originalText.toByteArray(), secretKey)
        val str_en_data = bytes2hexstr(encryptedData)
        Log.d(TAG, "Encrypted data:  ${str_en_data}")
        val bytes_en_data = hexstr2bytes(str_en_data)
        Log.d(TAG, "Encrypted data2: ${bytes2hexstr(bytes_en_data)}")
//        val decrbytes_en_datayptedData = aesDecrypt(encryptedData, secretKey)
        val decryptedData = aesDecrypt(bytes_en_data, secretKey)
        val decryptedText = String(decryptedData)

        Log.d(TAG, "Original text: ${originalText}")
        Log.d(TAG, "Encrypted text: ${encryptedData}")
        Log.d(TAG, "Decrypted text: ${decryptedText}")
    }




}

