package com.arpitjindal97.barcodescanner.utils

import android.content.Context
import android.preference.PreferenceManager
import android.util.Xml
import com.arpitjindal97.barcodescanner.R
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class ResultHolder @Inject constructor(private val context: Context){

    private val result = mutableListOf<String>()

    fun getString(): String {
        var str = ""
        for (c in result) {
            str = str + c + "\n"
        }

        str = str.substring(0, str.length - 1)
        return str
    }

    fun getArray(): Array<String> {
        return result.toTypedArray()
    }

    fun add(str: String) {
        result += str
    }


    fun parseResult(str: String) {
        result.clear()

        val scanTypeVal = context.resources.getStringArray(R.array.scan_type_list)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val value = sharedPreferences.getString("scan_type", scanTypeVal[0])


        if (value == scanTypeVal[0]) {
            add(str)
        } else if (value == scanTypeVal[1]) {
            try {
                parseXmlImeiQr(str)
            } catch (_: Exception) {
                add("Invalid QR Code, change the settings")
            }
        }
    }

    private fun parseXmlImeiQr(str: String) {

        val input = ByteArrayInputStream(str.toByteArray(StandardCharsets.UTF_8))

        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(input, null)
        parser.nextTag()

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {

            if (event == XmlPullParser.START_TAG) {
                if (parser.name == "IMEI") {
                    add(parser.nextText())
                }

            }
            event = parser.next()
        }

        input.close()

    }

}

