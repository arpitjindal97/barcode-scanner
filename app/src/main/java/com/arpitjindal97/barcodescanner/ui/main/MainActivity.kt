package com.arpitjindal97.barcodescanner.ui.main

import android.Manifest
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.arpitjindal97.barcodescanner.MyApplication
import com.arpitjindal97.barcodescanner.R
import com.arpitjindal97.barcodescanner.SettingsActivity
import com.arpitjindal97.barcodescanner.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        const val CAMERA_REQUEST = 1888
    }

    private var menu: Menu? = null
    private lateinit var viewModel: MainViewModel
    private lateinit var resultAlertBox: AlertDialog
    private lateinit var progressDialog: AlertDialog
    private lateinit var progressDialogView: View

    @Inject
    lateinit var viewModelFactory : ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        getPermissions()

        progressDialogView = layoutInflater.inflate(R.layout.progressbar_dialog,
                findViewById(android.R.id.content), false)

        progressDialogView.findViewById<Button>(R.id.progress_dialog_button).setOnClickListener {
            progressDialog.dismiss()
            resumeCameraPreview()
        }
        progressDialog = AlertDialog.Builder(this)
                .setView(progressDialogView)
                .setCancelable(false)
                .create()

        MyApplication.appComponent.inject(this)

        viewModel = ViewModelProviders.of(this,viewModelFactory).get(MainViewModel::class.java)
        viewModel.init()


        viewModel.getFlash().observe(this, Observer { _ ->
            toggleFlash()
        })
        viewModel.getScanResult().observe(this, Observer { result ->
            showResult(result.toString())
        })
        viewModel.getProgressStatus().observe(this, Observer { serverStatus ->
            updateProgress(serverStatus?.status.toString())
        })

        viewModel.getProgressCount().observe(this, Observer { count ->
            progressDialogView.findViewById<TextView>(R.id.progressCount).text = count
        })


    }


    override fun onCreateOptionsMenu(menuArg: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menuArg)
        this.menu = menuArg!!
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_flash -> {
                viewModel.clickFlash()
            }
            R.id.menu_setting -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        scanner_view?.setResultHandler(viewModel)
        scanner_view?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scanner_view?.stopCamera()
    }

    private fun getPermissions() {

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST)
        }
    }

    private fun resumeCameraPreview() {
        scanner_view.resumeCameraPreview(viewModel)
    }

    private fun toggleFlash() {
        if (viewModel.getFlash().value == true) {
            menu?.findItem(R.id.menu_flash)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_flash_on_white_24dp)
            scanner_view.flash = true
        } else {
            menu?.findItem(R.id.menu_flash)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_flash_off_white_24dp)
            scanner_view.flash = false
        }
    }

    private fun showResult(result: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Scan Result")
        builder.setMessage(result)

        builder.setPositiveButton("Send") { _: DialogInterface?, _: Int ->
            viewModel.sendButtonClicked()
        }

        builder.setOnDismissListener {
            if (viewModel.getProgressStatus().value?.status == "sent") {
                resumeCameraPreview()
            }
        }

        resultAlertBox = builder.create()

        resultAlertBox.show()
    }

    private fun updateProgress(status: String) {

        when (status) {
            "start_sending" -> {

                resultAlertBox.dismiss()
                progressDialogView.findViewById<TextView>(R.id.progress_title).text =
                        resources.getString(R.string.progress_title_sending)
                progressDialogView.findViewById<ProgressBar>(R.id.indeterminateBar).isIndeterminate = true
                progressDialogView.findViewById<ProgressBar>(R.id.indeterminateBar).progress = 0
                progressDialogView.findViewById<Button>(R.id.progress_dialog_button).isEnabled = false
                progressDialog.show()
            }
            "failed" -> {
                progressDialogView.findViewById<TextView>(R.id.progress_title).text =
                        resources.getString(R.string.progress_title_failed)
                progressDialogView.findViewById<ProgressBar>(R.id.indeterminateBar).isIndeterminate = false
                progressDialogView.findViewById<ProgressBar>(R.id.indeterminateBar).progress = 0
                progressDialogView.findViewById<Button>(R.id.progress_dialog_button).isEnabled = true

            }
            "sent" -> {
                progressDialogView.findViewById<TextView>(R.id.progress_title).text =
                        resources.getString(R.string.progress_title_sent)
                progressDialogView.findViewById<ProgressBar>(R.id.indeterminateBar).isIndeterminate = false
                progressDialogView.findViewById<ProgressBar>(R.id.indeterminateBar).progress = 100
                progressDialogView.findViewById<Button>(R.id.progress_dialog_button).isEnabled = true
            }
        }
    }
}