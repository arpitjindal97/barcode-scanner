package com.arpitjindal97.barcodescanner

class ResultHolder {

    private val result = mutableListOf<String>()

   fun getString(): String {
       var str = ""
       for (c in result) {
          str = str + c + "\n"
       }

       str = str.substring(0,str.length-1)
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
        add(str)
        add("something")
        add("skvbvk")
        add("arpit")
    }

}

