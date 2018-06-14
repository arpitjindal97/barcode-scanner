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

    private var tag: String = "arpit"

    private var flash: Boolean = false
    private var result = ResultHolder()
    var sendButtonPressed: Boolean = false
    private lateinit var progressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        getPermissions()

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

    private fun prepareProgressDialog() {
        val progressDialogView =
                layoutInflater.inflate(R.layout.progressbar_dialog, findViewById(android.R.id.content), false)
        val builder = AlertDialog.Builder(this)
        builder.setView(progressDialogView)
        progressDialog = builder.create()

        val str = "Sending Result..."

        progressDialogView.findViewById<TextView>(R.id.progress_title).text = str
        progressDialogView.findViewById<ProgressBar>(R.id.indeterminateBar).isIndeterminate = true
        progressDialogView.findViewById<ProgressBar>(R.id.indeterminateBar).progress = 0
        progressDialogView.findViewById<TextView>(R.id.progressCount).text = "0/0"
        progressDialogView.findViewById<Button>(R.id.progress_dialog_button).isEnabled = false
        progressDialogView.findViewById<Button>(R.id.progress_dialog_button).setOnClickListener {
            progressDialog.dismiss()
            resumeCameraPreview()
        }
    }

    private fun stopProgressDialog(result: String) {

        val str: String = if (result == "OK") {
            "Sent"
        } else {
            "Failed !"
        }
        progressDialog.findViewById<TextView>(R.id.progress_title).text = str
        progressDialog.findViewById<ProgressBar>(R.id.indeterminateBar).isIndeterminate = false
        progressDialog.findViewById<ProgressBar>(R.id.indeterminateBar).progress = 100
        progressDialog.findViewById<Button>(R.id.progress_dialog_button).isEnabled = true
    }

    override fun handleResult(rawResult: Result?) {

        // Do something with the result here
        result.parseResult(rawResult?.text.toString())

        Log.v(tag, rawResult?.text)
        Log.v(tag, rawResult?.barcodeFormat.toString())

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Scan Result")
        builder.setMessage(result.getString())

        builder.setPositiveButton("Send", sendButtonListener)

        builder.setOnDismissListener(dialogDismissListener)

        val alert1 = builder.create()

        alert1.show()

    }

    private fun resumeCameraPreview() {
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

    private fun getPermissions() {

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_REQUEST_CODE)
        }
    }

    private var dialogDismissListener = DialogInterface.OnDismissListener {
        if (!sendButtonPressed) {
            resumeCameraPreview()
        }
    }

    private var sendButtonListener = DialogInterface.OnClickListener { dialog: DialogInterface?, _: Int ->
        Log.v(tag, "button clicked")

        sendButtonPressed = true

        class MyAsyncTask : AsyncTask<Void, Int, String>() {


            override fun onPreExecute() {
                super.onPreExecute()
                dialog?.dismiss()
                prepareProgressDialog()
                progressDialog.show()
            }

            override fun doInBackground(vararg params: Void?): String {

                publishProgress(1)
                try {
                    if (serverStatus() == "Running") {
                        val array = result.getArray()
                        var i = 0

                        while (i < array.size) {
                            publishProgress(i + 1)
                            if (sendResult(array[i]) == array[i]) {
                                i++
                            }
                        }
                        return "OK"
                    }
                    return "Error"

                } catch (_: Exception) {
                    return "Error"
                }
            }

            override fun onProgressUpdate(vararg values: Int?) {
                val str = values[0].toString() + "/" + result.getArray().size.toString()
                progressDialog.findViewById<TextView>(R.id.progressCount).text = str
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                sendButtonPressed = false
                Log.v(tag, result)
                stopProgressDialog(result)
            }
        }

        MyAsyncTask().execute()

    }

    fun serverStatus(): String {
        val url = DatabaseHelper(baseContext).get().statusURL

        val request = Request.Builder()
                .url(url)
                .build()

        val client = OkHttpClient()

        val resp = client.newCall(request).execute()
        return resp.body()?.string().toString()

    }

    fun sendResult(str: String): String {

        val url = DatabaseHelper(this).get().resultURL

        val dataType: MediaType? = MediaType.parse("text/plain;charset=utf-8")


        val data = RequestBody.create(dataType, str)
        val request = Request.Builder().url(url).post(data).build()

        val resp = OkHttpClient().newCall(request).execute()
        return resp.body()?.string().toString()


    }

    companion object {
        var MY_CAMERA_REQUEST_CODE = 100

    }
}
