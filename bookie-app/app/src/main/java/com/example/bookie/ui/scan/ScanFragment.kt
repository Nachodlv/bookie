package com.example.bookie.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.bookie.R
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.fragment_scan.*
import java.io.IOException


class ScanFragment : Fragment() {

    private lateinit var scanViewModel: ScanViewModel
    var btnOpenCamera: Button? = null
    private var txtResultBody: TextView? = null

    private var detector: BarcodeDetector? = null
    private var imageUri: Uri? = null
    private val requestCameraPermission = 200
    private val cameraRequest = 101
    private val savedInstanceUri = "uri"
    private val savedInstanceResult = "result"

    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    var intentData = ""
    var isEmail = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        scanViewModel =
            ViewModelProviders.of(this).get(ScanViewModel::class.java)

        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
    }


    private fun initViews() {
        btnAction.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (intentData.length > 0) {
                    //TODO search
                    print("Searching...")
                }
            }
        })
    }

    private fun initialiseDetectorsAndSources() {
        val currentContext = context?: return
        Toast.makeText(context, "Barcode scanner started", Toast.LENGTH_SHORT)
            .show()
        barcodeDetector = BarcodeDetector.Builder(currentContext)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()
        cameraSource = CameraSource.Builder(currentContext, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
        surfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (checkSelfPermission(
                            currentContext,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource?.start(surfaceView!!.holder)
                    } else {
                        requestPermissions(
                            arrayOf<String>(Manifest.permission.CAMERA),
                            REQUEST_CAMERA_PERMISSION
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource?.stop()
            }
        })
        barcodeDetector?.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(
                    context,
                    "To prevent memory leaks barcode scanner has been stopped",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    txtBarcodeValue!!.post {
                        if (barcodes.valueAt(0).email != null) {
                            txtBarcodeValue!!.removeCallbacks(null)
                            intentData = barcodes.valueAt(0).email.address
                            txtBarcodeValue!!.text = intentData
                            isEmail = true
                            btnAction.text = "ADD CONTENT TO THE MAIL"
                        } else {
                            isEmail = false
                            btnAction.text = "LAUNCH URL"
                            intentData = barcodes.valueAt(0).displayValue
                            txtBarcodeValue!!.text = intentData
                        }
                    }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        cameraSource!!.release()
    }

    override fun onResume() {
        super.onResume()
        initialiseDetectorsAndSources()
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 201
    }


}