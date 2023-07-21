package com.example.notesapp.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date


data class Note(
    var id: String? = "",
    var user_id: String? = "",
    val title:String? = "",
    val description:String? = "",
    @ServerTimestamp
    val date: Date = Date()

)
