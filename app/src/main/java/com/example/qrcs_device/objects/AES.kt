package com.example.qrcs_device.objects

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import javax.crypto.Cipher
import java.io.UnsupportedEncodingException
import java.security.NoSuchAlgorithmException
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.util.*

object AESDemo {
    private  val TAG = "SecurityClass"
    private var secretKey: SecretKeySpec? = null
    private lateinit var key: ByteArray

    // set Key
    fun setKey(myKey: String) {
        var sha: MessageDigest? = null
        try {
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

    // method to encrypt the secret text using key
    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(strToEncrypt: String, secret: String): String? {
        try {
            setKey(secret)
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return Base64.getEncoder().encodeToString(cipher.doFinal
                (strToEncrypt.toByteArray(charset("UTF-8"))))
        } catch (e: Exception) {

            println("Error while encrypting: $e")
        }
        return null
    }

    // method to encrypt the secret text using key
    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(strToDecrypt: String?, secret: String): String? {
        try {
            setKey(secret)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return String(cipher.doFinal(Base64.getDecoder().
            decode(strToDecrypt)))
        } catch (e: Exception) {
            println("Error while decrypting: $e")
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun test(args: Array<String>) {
        // key
        val secretKey = "develop00develop"
        // secret text
        val originalString = "1234567890"
        // Encryption
        val encryptedString = encrypt(originalString, secretKey)
        // Decryption
        val decryptedString = decrypt(encryptedString, secretKey)
        // Printing originalString,encryptedString,decryptedString
        Log.d(TAG, "Original String:$originalString")
        Log.d(TAG,"Encrypted value:$encryptedString")
        Log.d(TAG,"Decrypted value:$decryptedString")
    }
}