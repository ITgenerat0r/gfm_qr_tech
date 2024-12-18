package com.example.qrcs_device

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.Image
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
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
import com.example.qrcs_device.adapters.ButtonsAdapter
import com.example.qrcs_device.objects.Device
import com.example.qrcs_device.objects.OpBtn
import com.example.qrcs_device.objects.Operation
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import java.io.File
import java.io.FileOutputStream


class readQR : AppCompatActivity() {

    private val TAG = "QRactivity"
//    private lateinit var previewView: PreviewView
//    private lateinit var imageCapture: ImageCapture
    
    private lateinit var res_txt: TextView
    private lateinit var txt_info: TextView
    private lateinit var btn_take: ImageButton
    private lateinit var list_btns: ListView
    private lateinit var btn_decrease: ImageView
    private lateinit var btn_increase: ImageView

    private var capture: CaptureManagerCustom? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null


    lateinit var cntr: Controller


    val pref = SharedPreference(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        pref.set_bool("restartQR", true)
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
        Log.d(TAG, "toolbar done.")

        list_btns = findViewById(R.id.list_btns)
        btn_take = findViewById(R.id.btn_action)
//        btn_take.setBackgroundColor(resources.getColor(R.color.main_color))
        btn_take.background.setTint(resources.getColor(R.color.main_color))

        btn_decrease = findViewById<ImageView>(R.id.btn_decrease)
        btn_increase = findViewById<ImageView>(R.id.btn_increase)
        btn_decrease.setColorFilter(resources.getColor(R.color.main_color))
        btn_increase.setColorFilter(resources.getColor(R.color.main_color))
        btn_decrease.setOnClickListener {
            Log.d(TAG, "decrease")
            serial_number_add(-1)
        }
        btn_increase.setOnClickListener {
            Log.d(TAG, "increase")
            serial_number_add(1)
        }


        txt_info = findViewById(R.id.txt_info)
        res_txt = findViewById<TextView>(R.id.txt_res)
        var sn = pref.get_str("QR_result")
        Log.d(TAG, "QR RESULT: $sn")
        var sn_data = sn.split("|")
        pref.set_str("serial_number_str", sn_data[0])
        Log.d(TAG, "serial_number: ${sn_data[0]}")
        if (sn_data.size > 1){
            pref.set_str("decimal", sn_data[1])
            Log.d(TAG, "decimal_number: ${sn_data[1]}")
        } else {
            pref.set_str("decimal", "")
        }
        if (sn_data.size > 2){
            pref.set_str("device_name", sn_data[2])
            Log.d(TAG, "device_name: ${sn_data[2]}")
        } else {
            pref.set_str("device_name", "")
        }

        setup_serial_number(sn_data[0])



//        Log.d(TAG, "pre")
//        previewView = findViewById<PreviewView>(R.id.previewView)
//        Log.d(TAG, "capture")
//        imageCapture = ImageCapture.Builder().build()

        requestCameraPermission()







        btn_take.setOnClickListener {
//            val intentIntegrator = IntentIntegrator(this)
//            intentIntegrator.setBeepEnabled(false)
//            intentIntegrator.setPrompt("Scan a barcode or QR code")
//            intentIntegrator.setOrientationLocked(false)
//            intentIntegrator.setBarcodeImageEnabled(false)
//            intentIntegrator.setCaptureActivity(QR_taker::class.java)
//            intentIntegrator.initiateScan()
//            finish()
//            onCreate(savedInstanceState)
//            res_txt.setText("--------")
            val a = pref.get_str("btn_action")
//            Toast.makeText(baseContext, "<$a>", Toast.LENGTH_SHORT).show()
            if (a == "goto"){
                val intent = Intent(this, DeviceActivity::class.java)
                startActivity(intent)
            } else if (a == "add"){
                // need access

                pref.set_str("add_action", "add")
                val intent = Intent(this, AddDeviceActivity::class.java)
                startActivity(intent)
            }
        }



        Log.d(TAG, "onCreate() Done!")
    }


    private fun setup_serial_number(sn: String){
        Log.d(TAG, "setup_serial_number $sn")
        txt_info.setText("")
        if (sn == ""){
            res_txt.setText("--------")
            btn_take.visibility = View.GONE
            btn_decrease.visibility = View.GONE
            btn_increase.visibility = View.GONE
        } else {
            btn_take.visibility = View.VISIBLE
            btn_decrease.visibility = View.VISIBLE
            btn_increase.visibility = View.VISIBLE
            pref.set_str("QR_result", "")



            val groups: ArrayList<String> = arrayListOf()
            val login = pref.get_str("login")

            cntr = Controller(this)
            // request sn data
            // ========== DEVICE ==================================================
            val rx = cntr.send("getdevicedata ${sn}")
            Log.d(TAG, rx)
            if (rx == "none" || rx == "error"){
                txt_info.setText(getString(R.string.device_not_exist_in_database))
                // get user groups here ...
                val groups_rx = cntr.send("getworkergroups ${pref.get_str("login")}")
                for (g in groups_rx.split('|')){
                    groups.add(g)
                }
                if ("admins" in groups || "editors" in groups || "workers" in groups){
                    btn_take.setImageResource(R.drawable.baseline_add_24)
                    pref.set_str("btn_action", "add")
                } else {
                    btn_take.visibility = View.GONE
                }
                val adapter = ButtonsAdapter(this, arrayListOf())
                list_btns.adapter = adapter
                adapter.notifyDataSetChanged()


            } else {
                btn_take.setImageResource(R.drawable.baseline_arrow_right_alt_24)
                pref.set_str("btn_action", "goto")

                val device = Device(sn.toInt())
                for (i in rx.split('|')){
                    val row = i.split(':')
                    device.set_field(row[0], row[1])
                    Log.d(TAG, "Row: ${row[0]}, ${row[1]}.")
                }



                // ========== OPERATIONS ==============================================
                val op_types = cntr.send("getoperationtypes")
                var operation_types: MutableList<String> = mutableListOf()
                var operations: ArrayList<Operation> = arrayListOf()
                for (st in op_types.split('|')){
                    operation_types.add(st)
                }
                val oper_data = cntr.send("getdeviceoperations ${sn}")
                Log.d(TAG, "oper_data")
                Log.d(TAG, oper_data)
                if (oper_data != "none" && oper_data != "error"){
//                    val title_op = Operation()
//                    title_op.set_date(getString(R.string.date))
//                    title_op.set_worker(getString(R.string.worker))
//                    title_op.set_operation(getString(R.string.operation))
//                    title_op.set_btn_type("none")
//
//                    operations.add(title_op)
                    for(op in oper_data.split('•')){
                        val operation = Operation()
                        for (row in op.split('|')){
                            val key = row.split(':')
                            Log.d(TAG, "${key[0]}: ${key[1]}")
                            operation.set_field(key[0], key[1])
                        }
                        if ("editors" in groups || "admins" in groups){
                            operation.set_editable(true)
                            operation.set_operation_types(operation_types)
                            operation.set_btn_type("delete")
                        } else if ("workers" in groups){
                            if (login == operation.get_worker()){
                                operation.set_editable(true)
                                operation.set_operation_types(operation_types)
                                operation.set_btn_type("delete")
                            }
                        }
                        if(operation.get_operation() in operation.get_operation_types() == false){
                            val ltp = operation.get_operation_types()
                            ltp.add(operation.get_operation())
                            operation.set_operation_types(ltp)
                        }

                        Log.d(TAG, "=====================")
                        Log.d(TAG, operation.toString())
                        Log.d(TAG, "=====================")
                        operations.add(operation)
                    }
                }
                // log
                Log.d(TAG, "count operations: ${operations.size}")

                // set adapter here
                val btns = mutableMapOf<String, OpBtn>()
                if (pref.get_bool("CAC")){
//                    btns.add(resources.getString(R.string.txt_cac))
                    btns["Проверка после заливки"] = OpBtn("Проверка после заливки")
                }
                if (pref.get_bool("CHK")){
//                    btns.add(resources.getString(R.string.txt_chk))
                    btns["Проверка"] = OpBtn("Проверка")
                }
                if (pref.get_bool("DBG")){
//                    btns.add(resources.getString(R.string.txt_debug))
                    btns["Отладка"] = OpBtn("Отладка")
                }
                if (pref.get_bool("CLI")){
//                    btns.add(resources.getString(R.string.txt_cli))
                    btns["Климат"] = OpBtn("Климат")
                }
                if (pref.get_bool("ABC")){
//                    btns.add(resources.getString(R.string.txt_abc))
                    btns["Сборка до заливки"] = OpBtn("Сборка до заливки")
                }
                if (pref.get_bool("AAC")){
//                    btns.add(resources.getString(R.string.txt_aac))
                    btns["Сборка после заливки"] = OpBtn("Сборка после заливки")
                }
                if (pref.get_bool("CAL") ){
//                    btns.add(resources.getString(R.string.txt_cal))
                    btns["Калибровка"] = OpBtn("Калибровка")
                }
                for (op in operations){
                    Log.d(TAG, "Operation: $op")
                    if (op.get_operation() in btns){
                        btns[op.get_operation()]?.add(baseContext, op)
                    }

                }
                // ====================================================================

                val mbtns = arrayListOf<OpBtn>()
                for ((nmbtn, btn) in btns){
                    mbtns.add(btn)
                }

                val adapter = ButtonsAdapter(this, mbtns, login, sn)
                list_btns.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
        res_txt.setText(sn)

    }

    private fun serial_number_add(k: Int){
        var sn = res_txt.text.toString()
        Log.d(TAG, "number: $sn")
        if (sn != ""){
            Log.d(TAG, "not empty")
            try {
                val nn = sn.toInt() + k
                Log.d(TAG, "new number: $nn")
                if (nn > 0){
                    res_txt.setText("$nn")
                    val pref = SharedPreference(this)
                    pref.set_int("serial_number", nn)
                    pref.set_str("serial_number_str", nn.toString())
                    setup_serial_number(nn.toString())
                }
                Log.d(TAG, "done.")
            } catch (e: Exception){
                Log.d(TAG, "wrong number")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        capture?.onResume()
        Log.d(TAG, "onResume()")
        serial_number_add(0)

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
//            openCamera()
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
//            openCamera()
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


//    private fun openCamera() {
//        Log.d(TAG, "openCamera()")
//        return
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            val cameraProvider = cameraProviderFuture.get()
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//            val preview = Preview.Builder().build()
//            val imageCapture = ImageCapture.Builder().build()
//
//            try {
////                cameraProvider.unbindAll()
//                val camera = cameraProvider.bindToLifecycle(
//                    this,
//                    cameraSelector,
//                    preview,
//                    imageCapture
//                )
////                val tt = previewView.surfaceProvider(camera)
////                preview.setSurfaceProvider(previewView.createSurfaceProvider(camera.cameraInfo))
//                preview.setSurfaceProvider(previewView.surfaceProvider)
//            } catch (exception: Exception) {
//                // Handle camera setup errors
//            }
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun captureImage() {
//        val file = File(externalMediaDirs.first(), "image.jpg")
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
//
//        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                    // Image saved successfully
//                }
//
//                override fun onError(exception: ImageCaptureException) {
//                    // Handle image capture errors
//                }
//            })
//    }
//
//    private fun saveImage(image: Image) {
//        val buffer = image.planes[0].buffer
//        val bytes = ByteArray(buffer.remaining())
//        buffer.get(bytes)
//
//        val file = File(externalMediaDirs.first(), "image.jpg")
//        FileOutputStream(file).use { output ->
//            output.write(bytes)
//        }
//    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        pref.set_bool("restartQR", false)
        super.onBackPressed()
    }

}


