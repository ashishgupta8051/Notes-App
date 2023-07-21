package com.example.notesapp.model

data class Response(
    var code:Int? = null,
    var message:String? = null,
    var user:User? = null,
    var note:Note? = null,
)
