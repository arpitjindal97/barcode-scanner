package com.arpitjindal97.barcodescanner

import android.content.Context
import android.preference.PreferenceManager

class Server {

    companion object {


        fun statusURL( context: Context): String {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ip = sharedPreferences.getString("ip_address", "default")
            val port = sharedPreferences.getString("port_number", "default")

            return "http://$ip:$port/Status"
        }


        fun resultURL(context: Context): String {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ip = sharedPreferences.getString("ip_address", "default")
            val port = sharedPreferences.getString("port_number", "default")

            return "http://$ip:$port/Result"
        }
    }
}