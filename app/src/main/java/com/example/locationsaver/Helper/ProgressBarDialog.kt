package com.example.locationsaver.Helper

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.locationsaver.R

object ProgressBarDialog {
    lateinit var  dialog: Dialog
    fun createProgressDialog(
        context: Context,
        cancelable: Boolean,
        mainTextViewText: String,
        subTextViewText: String
    ) {
        dialog = Dialog(context)
        val view=LayoutInflater.from(context).inflate(R.layout.progress_dialog_layout,null)
        dialog.setContentView(view)
        val mainTextView = view.findViewById<TextView>(R.id.progressDialog_TV_mainTextView)
        val sub = view.findViewById<TextView>(R.id.progressDialog_TV_subTextView)
        mainTextView.text = mainTextViewText
        sub.text = subTextViewText
        dialog.setCancelable(cancelable)

    }

    fun showProgressDialog() {
        dialog.show()

    }

    fun dismissProgressDialog() {
        dialog.dismiss()
    }
}