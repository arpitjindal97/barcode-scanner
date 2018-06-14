package com.arpitjindal97.barcodescanner

class Server(ip: String, port: String) {

    var ipAddress: String = ip
    var portNumber: String = port

    var url: String = "temp"
        get() = "$ipAddress:$portNumber"

    var resultURL = ""
        get() = "http://$url/Result"

    var statusURL = ""
        get() = "http://$url/Status"
}