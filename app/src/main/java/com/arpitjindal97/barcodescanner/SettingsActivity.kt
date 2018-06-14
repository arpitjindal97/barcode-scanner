package com.arpitjindal97.barcodescanner

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.preference.*
import android.view.MenuItem


class SettingsActivity : AppCompatPreferenceActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()

        db = DatabaseHelper(baseContext)
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
    }


    class ServerPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)

            val server = db.get()

            (findPreference("ip_address") as EditTextPreference).text = server.ipAddress
            (findPreference("port_number") as EditTextPreference).text = server.portNumber

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


    companion object {

        lateinit var db: DatabaseHelper
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()


            if (preference.key == "ip_address") {
                db.update(Server(stringValue, db.get().portNumber))
            } else {
                db.update(Server(db.get().ipAddress, stringValue))
            }

            // simple string representation.
            preference.summary = stringValue
            true
        }

        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
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
    }
}
