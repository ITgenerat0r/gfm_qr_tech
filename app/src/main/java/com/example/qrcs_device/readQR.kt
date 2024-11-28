package com.example.qrcs_device

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.Image
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import java.io.File
import java.io.FileOutputStream


class readQR : AppCompatActivity() {

    private val TAG = "QRactivity"
    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture
    
    private lateinit var res_txt: TextView
    private lateinit var btn_take: Button

    private var capture: CaptureManagerCustom? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_qr)
        Log.d(TAG, "barcode init...")
        barcodeScannerView = initializeContent()

        Log.d(TAG, "capture init...")
        capture = CaptureManagerCustom(this, barcodeScannerView!!)
        if (capture != null){
            Log.d(TAG, "capture not null.")
            capture!!.initializeFromIntent(getIntent(), savedInstanceState)
            capture!!.decode()
        }
        Log.d(TAG, "init done.")

        window.statusBarColor = resources.getColor(R.color.black)

        val toolbar: Toolbar = findViewById(R.id.toolbar_read_qr)
        setSupportActionBar(toolbar)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)
//        toolbar.title = resources.getString(R.string.settings)
//        toolbar.setTitleTextColor(resources.getColor(R.color.black))
        toolbar.setBackgroundColor(resources.getColor(R.color.main_color))




        Log.d(TAG, "pre")
        previewView = findViewById<PreviewView>(R.id.previewView)
        Log.d(TAG, "capture")
        imageCapture = ImageCapture.Builder().build()

        requestCameraPermission()




        res_txt = findViewById<TextView>(R.id.txt_res)
        btn_take = findViewById<Button>(R.id.button)
        btn_take.setBackgroundColor(resources.getColor(R.color.main_color))
        btn_take.setOnClickListener {
//            val intentIntegrator = IntentIntegrator(this)
//            intentIntegrator.setBeepEnabled(false)
//            intentIntegrator.setPrompt("Scan a barcode or QR code")
//            intentIntegrator.setOrientationLocked(false)
//            intentIntegrator.setBarcodeImageEnabled(false)
//            intentIntegrator.setCaptureActivity(QR_taker::class.java)
//            intentIntegrator.initiateScan()
            res_txt.setText("back")
            finish()
        }



        Log.d(TAG, "onCreate() Done!")
    }

    override fun onResume() {
        super.onResume()
        capture?.onResume()

    }

    override fun onPause() {
        super.onPause()
        capture?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        capture?.onSaveInstanceState(outState)
    }


    protected fun initializeContent(): DecoratedBarcodeView {
//        setContentView(com.google.zxing.client.android.R.layout.zxing_capture)
//        return findViewById(com.google.zxing.client.android.R.id.zxing_barcode_scanner)
        setContentView(R.layout.activity_read_qr)
        return findViewById(R.id.qr_view)
    }




//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        val res = IntentIntegrator.parseActivityResult(resultCode, data)
//        if (res != null){
//            if (res.contents == null){
//                Toast.makeText(baseContext, "Cancelled", Toast.LENGTH_SHORT).show()
//            } else {
//                res_txt.setText("res: ${res.contents} | ${res.formatName}")
//            }
//        }
//    }





//            AlertDialog.Builder(this).setMessage("Would you like to go to ${res.contents}?")
//                .setPositiveButton("Accept", DialogInterface.OnClickListener{
//                        dialogInterface, i -> val intent = Intent(Intent.ACTION_WEB_SEARCH)
//                    intent.putExtra(SearchManager.QUERY, res.contents)
//                    startActivity(intent)
//                })
//                .setNegativeButton("Deny", DialogInterface.OnClickListener{ dialogInterface, i ->  })
//                .create()
//                .show()

    private val cameraPermissionRequestCode = 100

    private fun requestCameraPermission() {
        Log.d(TAG, "requestCameraPermission()")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionRequestCode
            )
        } else {
            // Permission already granted
            Log.d(TAG, "Already granted!")
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == cameraPermissionRequestCode && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            // Permission denied
            finish()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        capture?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun isCameraAvailable(): Boolean {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIds = cameraManager.cameraIdList
        return cameraIds.isNotEmpty()
    }


    private fun openCamera() {
        Log.d(TAG, "openCamera()")
        return
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val preview = Preview.Builder().build()
            val imageCapture = ImageCapture.Builder().build()

            try {
//                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
//                val tt = previewView.surfaceProvider(camera)
//                preview.setSurfaceProvider(previewView.createSurfaceProvider(camera.cameraInfo))
                preview.setSurfaceProvider(previewView.surfaceProvider)
            } catch (exception: Exception) {
                // Handle camera setup errors
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImage() {
        val file = File(externalMediaDirs.first(), "image.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Image saved successfully
                }

                override fun onError(exception: ImageCaptureException) {
                    // Handle image capture errors
                }
            })
    }

    private fun saveImage(image: Image) {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val file = File(externalMediaDirs.first(), "image.jpg")
        FileOutputStream(file).use { output ->
            output.write(bytes)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}


