package com.example.bookie.ui.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.bookie.R
import com.example.bookie.models.Book
import com.example.bookie.repositories.BookRepository
import com.example.bookie.repositories.RepositoryStatus
import com.example.bookie.ui.book_profile.BookProfile
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.fragment_scan.*
import java.io.IOException


class ScanFragment : Fragment() {

    private lateinit var scanViewModel: ScanViewModel

    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null

    private val injector = KodeinInjector()
    private val bookRepository: BookRepository by injector.instance()

    private var previousIsbn: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        scanViewModel =
            ViewModelProviders.of(this).get(ScanViewModel::class.java)

        injector.inject(appKodein())

        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    private fun initialiseDetectorsAndSources() {
        val currentContext = context ?: return
        Toast.makeText(context, "Please scan a book", Toast.LENGTH_SHORT)
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

            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    if (barcodes.valueAt(0).rawValue != null) {
                        val isbn = barcodes.valueAt(0).rawValue
                        if (previousIsbn == isbn) return
                        previousIsbn = isbn
                        activity!!.runOnUiThread {
                            showToast(getString(R.string.code_detected))
                            observeSearchResult(bookRepository.searchByIsbn(isbn))
                        }
                    }
                }
            }
        })
    }


    private fun observeSearchResult(result: LiveData<RepositoryStatus<Book>>) {
        result.observe(this, Observer {
            when (it) {
                is RepositoryStatus.Success -> {
                    val context = context?:return@Observer
                    val intent = Intent(context, BookProfile::class.java)
                    val bundle = Bundle()
                    bundle.putString("bookId", it.data.id)
                    intent.putExtras(bundle)
                    ContextCompat.startActivity(context, intent, bundle)
                }
                is RepositoryStatus.Loading -> return@Observer
                is RepositoryStatus.Error -> {
                    previousIsbn = null
                    showToast(it.error)
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


    private fun showToast(message: String) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}