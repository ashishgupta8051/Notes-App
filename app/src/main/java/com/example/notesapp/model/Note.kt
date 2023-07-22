package com.example.notesapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Note(
    var id: String? = "",
    var user_id: String? = "",
    val title:String? = "",
    val description:String? = "",
    val date: Date = Date(),
    val images: List<String> = arrayListOf(),
    val text_style:String? = ""
    ): Parcelable
