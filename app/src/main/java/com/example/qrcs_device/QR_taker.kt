package com.example.qrcs_device

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView

//class QR_taker : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_qr_taker)
//    }
//}

//package example.zxing

//import android.R
//import android.view.View
//import com.journeyapps.barcodescanner.CaptureActivity
//import com.journeyapps.barcodescanner.DecoratedBarcodeView

/**
 * This activity has a margin.
 */
class QR_taker : CaptureActivity() {
    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_qr_taker)
        return findViewById<View>(R.id.qr_taker) as DecoratedBarcodeView
    }
}