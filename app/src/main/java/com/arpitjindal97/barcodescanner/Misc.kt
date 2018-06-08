package com.arpitjindal97.barcodescanner

class Server {

    constructor() {}
    constructor(ip: String, port: String) {
        ip_address = ip
        port_number = port
    }

    lateinit var ip_address: String
    lateinit var port_number: String

    var url: String = "temp"
        get() = "$ip_address:$port_number"

    var ResultURL = ""
        get() = "http://$url/Result"

    var StatusURL = ""
        get() = "http://$url/Status"
}