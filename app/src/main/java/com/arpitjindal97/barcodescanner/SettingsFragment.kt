package com.arpitjindal97.barcodescanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.view.MenuItem
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder

class SettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_settings)

        bindPreferenceSummaryToValue(findPreference("ip_address"))
        bindPreferenceSummaryToValue(findPreference("port_number"))
        bindPreferenceSummaryToValue(findPreference("scan_type"))

        bindPreferenceOnClickListener(findPreference("open_source_lib"))
        bindPreferenceOnClickListener(findPreference("author"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {

            activity.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
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

        private fun bindPreferenceOnClickListener(preference: Preference) {
            preference.setOnPreferenceClickListener { preference ->

                if (preference.key == "open_source_lib") {
                    LibsBuilder()
                            .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                            .withActivityTitle("Open source libraries")
                            .withAboutIconShown(true)
                            .withAboutVersionShown(true)
                            .withAboutDescription(preference.context.resources.getString(R.string.app_description))
                            .start(preference.context)
                } else if (preference.key == "author") {

                    val browserIntent = Intent(Intent.ACTION_VIEW,
                            Uri.parse(preference.context.resources.getString(R.string.author_url)))
                    preference.context.startActivity(browserIntent)
                }
                true
            }
        }
    }
}