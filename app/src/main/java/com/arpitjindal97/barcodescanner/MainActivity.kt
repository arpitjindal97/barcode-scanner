package com.arpitjindal97.barcodescanner

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_main.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody


class MainActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private var TAG: String = "arpit"

    private var flash: Boolean = false
    var scan_result: String? = null

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

        scan_result = rawResult?.text
        Log.v(TAG, rawResult?.text)
        Log.v(TAG, rawResult?.barcodeFormat.toString())

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Scan Result")
        builder.setMessage(scan_result)
        builder.setPositiveButton("Send", sendButtonListener)

        var alert1 = builder?.create()

        alert1.show()

    }

    fun ResumeCameraPreview() {
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

        lateinit var progressdialog: AlertDialog

        class MyAsyncTask() : AsyncTask<Void, Void, String>() {

            override fun doInBackground(vararg params: Void?): String {

                return if (ServerStatus() == "Running") {
                    SendResult(scan_result)
                } else {
                    "Error"
                }
            }

            override fun onPreExecute() {
                super.onPreExecute()
                dialog?.dismiss()
                var progressdialog_view = layoutInflater.inflate(R.layout.progressbar_dialog, null)
                var builder = AlertDialog.Builder(this@MainActivity)
                builder.setView(progressdialog_view)
                progressdialog = builder?.create()
                progressdialog.show()
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                Log.v(TAG, result)

                progressdialog.findViewById<TextView>(R.id.progress_title).text = "Sent"
                progressdialog.findViewById<ProgressBar>(R.id.indeterminateBar).isIndeterminate = false
                progressdialog.findViewById<ProgressBar>(R.id.indeterminateBar).progress = 100
                progressdialog.findViewById<Button>(R.id.progress_dialog_button).isEnabled = true
                progressdialog.findViewById<Button>(R.id.progress_dialog_button).setOnClickListener {
                    progressdialog.dismiss()
                    ResumeCameraPreview()
                }

            }


        }

        var task = MyAsyncTask().execute()

    }

    fun ServerStatus(): String {
        var url = DatabaseHelper(baseContext).Get().StatusURL

        var request = Request.Builder()
                .url(url)
                .build()

        var client = OkHttpClient()
        var resp = client.newCall(request).execute()
        return resp.body()?.string().toString()

    }

    fun SendResult(result: String?): String {

        var url = DatabaseHelper(this).Get().ResultURL

        var dataType: MediaType? = MediaType.parse("text/plain;charset=utf-8")

        var data = RequestBody.create(dataType, result)
        var request = Request.Builder().url(url).post(data).build()

        var resp = OkHttpClient().newCall(request).execute()

        return resp.body()?.string().toString()

    }

    companion object {
        var MY_CAMERA_REQUEST_CODE = 100

    }
}
