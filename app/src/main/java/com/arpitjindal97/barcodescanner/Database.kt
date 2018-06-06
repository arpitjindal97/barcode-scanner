package com.arpitjindal97.barcodescanner

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper: SQLiteOpenHelper {

    constructor(context: Context) : super(context,"barcode_scanner_arpit",null,1)


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.v("arpit","on upgrade called")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        var query = "CREATE TABLE server ( IP_Address TEXT, Port_Number TEXT )"
        db?.execSQL(query)

        var values  = ContentValues()
        values.put("IP_Address","ip address of server")
        values.put("Port_Number","port number of server")

        db?.insert("server",null,values)

    }

    fun Get() : Server {

        var db = writableDatabase

        var cursor = db.rawQuery("select * from server",null)

        cursor?.moveToFirst()

        return Server(cursor.getString(0),cursor.getString(1))

    }
    fun Update(server: Server) {
        var db: SQLiteDatabase = writableDatabase

        var old = Get()

        var values = ContentValues()
        values.put("IP_Address",server.ip_address)
        values.put("Port_Number",server.port_number)

        db.update("server",values,"IP_Address=? AND Port_Number=?", arrayOf(old.ip_address,old.port_number))

    }


}