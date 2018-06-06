package com.arpitjindal97.barcodescanner

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_main.*
import me.dm7.barcodescanner.zxing.ZXingScannerView


class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var TAG: String = "arpit"

    private var flash: Boolean = false

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        GetPermissions()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_flash -> {
                toggleFlash(item)
            }
            R.id.menu_setting -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    private fun toggleFlash(item: MenuItem) {

        if (flash) {
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_flash_off_white_24dp)
            scanner_view.flash = false
        } else {
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_flash_on_white_24dp)
            scanner_view.flash = true
        }

        flash = !flash

    }

    override fun handleResult(rawResult: Result?) {
        // Do something with the result here
        Log.v(TAG, rawResult?.text)
        Log.v(TAG, rawResult?.barcodeFormat.toString())

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Scan Result")
        builder.setMessage(rawResult?.text)
        builder.setPositiveButton("Send", sendButtonListener)
        var alert1: AlertDialog = builder?.create()
        alert1.show()

        scanner_view?.resumeCameraPreview(this)
    }

    override fun onResume() {
        super.onResume()
        scanner_view?.setResultHandler(this)
        scanner_view?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scanner_view?.stopCamera()
    }

    fun GetPermissions() {

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_REQUEST_CODE)
        }
    }

    private var sendButtonListener = DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
        Log.v("arpit", "button clicked")

    }

    companion object {
        var MY_CAMERA_REQUEST_CODE = 100

    }
}
