package com.arpitjindal97.barcodescanner

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.preference.*
import android.view.MenuItem
import android.widget.Toast


class SettingsActivity : AppCompatPreferenceActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()


        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val str = sharedPreferences.getString("scan_type", "default value")
        Toast.makeText(this, str, Toast.LENGTH_LONG).show()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == android.R.id.home) {

            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }


    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || ServerPreferenceFragment::class.java.name == fragmentName
                || ScanTypePreferenceFragment::class.java.name == fragmentName
    }

    class ServerPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_server)

            bindPreferenceSummaryToValue(findPreference("ip_address"))
            bindPreferenceSummaryToValue(findPreference("port_number"))
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {

                activity.onBackPressed()
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    class ScanTypePreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_scan_type)

            bindPreferenceSummaryToValue(findPreference("scan_type"))
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {

                activity.onBackPressed()
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }


    companion object {

        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()


            // simple string representation.
            preference.summary = stringValue
            true
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }

        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }


    }
}
