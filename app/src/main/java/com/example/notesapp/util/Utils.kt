package com.example.notesapp.util

import android.content.Context
import android.util.Log
import android.widget.Toast

fun showShortToast(context: Context, data:String){
    Toast.makeText(context,data,Toast.LENGTH_SHORT).show()
}

fun showLog(data:String){
    Log.e("TAG",data)
}


fun String.isValidEmail() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
