package com.arpitjindal97.barcodescanner

class Server {

    constructor() {}
    constructor(ip: String, port: String) {
        ip_address  = ip
        port_number = port
    }
     lateinit  var ip_address: String
    lateinit  var port_number: String
}