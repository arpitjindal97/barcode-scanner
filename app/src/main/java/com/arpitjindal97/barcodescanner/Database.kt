package com.arpitjindal97.barcodescanner

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "barcode_scanner_arpit", null, 1) {


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.v("arpit","on upgrade called")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE server ( IP_Address TEXT, Port_Number TEXT )"
        db?.execSQL(query)

        val values  = ContentValues()
        values.put("IP_Address","ip address of server")
        values.put("Port_Number","port number of server")

        db?.insert("server",null,values)

    }

    fun get() : Server {

        val db = writableDatabase

        val cursor = db.rawQuery("select * from server",null)

        cursor?.moveToFirst()

        val server = Server(cursor.getString(0),cursor.getString(1))
        cursor.close()
        return server

    }
    fun update(server: Server) {
        val db: SQLiteDatabase = writableDatabase

        val old = get()

        val values = ContentValues()
        values.put("IP_Address",server.ipAddress)
        values.put("Port_Number",server.portNumber)

        db.update("server",values,"IP_Address=? AND Port_Number=?", arrayOf(old.ipAddress,old.portNumber))

    }


}