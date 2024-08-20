package com.example.qrcs_device

import android.util.Log
import com.example.qrcs_device.objects.AESDemo
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
    private var secretKey: SecretKeySpec? = null
    
    init {
        aes_key = "develop"
    }


    fun generateAESKey(keySize: Int = 256): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(keySize)
        Log.d(TAG, "keygenerator: ${keyGenerator}")
        return keyGenerator.generateKey()
    }

    fun setKey(myKey: String) {
        var sha: MessageDigest? = null
        try {

            var key: ByteArray
            key = myKey.toByteArray(charset("UTF-8"))
            sha = MessageDigest.getInstance("SHA-1")
            key = sha.digest(key)
            key = Arrays.copyOf(key, 16)
            secretKey = SecretKeySpec(key, "AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    fun aesEncrypt(data: ByteArray, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParameterSpec = IvParameterSpec(ByteArray(16)) // Use a secure IV in production
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(data)
    }

    fun aesDecrypt(encryptedData: ByteArray, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParameterSpec = IvParameterSpec(ByteArray(16)) // Use the same IV as used in encryption
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
        return cipher.doFinal(encryptedData)
    }


    fun test() { // debug
        val originalText = "Hello Kotlin AES Encryption!"
        val secretKey = generateAESKey(256)

        val encryptedData = aesEncrypt(originalText.toByteArray(), secretKey)
        val decryptedData = aesDecrypt(encryptedData, secretKey)
        val decryptedText = String(decryptedData)

        Log.d(TAG, "Original text: ${originalText}")
        Log.d(TAG, "Decrypted text: ${decryptedText}")
    }




}

