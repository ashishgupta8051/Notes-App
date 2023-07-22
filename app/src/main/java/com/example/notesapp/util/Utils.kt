package com.example.notesapp.util

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun showShortToast(context: Context, data:String){
    Toast.makeText(context,data,Toast.LENGTH_SHORT).show()
}

fun showLog(data:String){
    Log.e("TAG",data)
}


fun String.isValidEmail() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun showMsg(msg: String, tvError: AppCompatTextView, lifecycleScope: LifecycleCoroutineScope) {
    tvError.visibility = View.VISIBLE
    tvError.text = msg
    lifecycleScope.launch(Dispatchers.Main) {
        delay(2000)
        tvError.visibility = View.GONE
        tvError.text = ""
    }
}

