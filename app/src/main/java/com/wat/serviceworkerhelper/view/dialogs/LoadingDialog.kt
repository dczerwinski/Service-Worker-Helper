package com.wat.serviceworkerhelper.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView
import androidx.annotation.StyleRes
import com.wat.serviceworkerhelper.R

class LoadingDialog(
    context: Context,
    @StyleRes themeResId: Int,
    private val loadingMessageText: String = ""
) : Dialog(context, themeResId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_loading, findViewById(R.id.loading_content))
        if (loadingMessageText.isNotEmpty()) {
            val loadingMessage: TextView = view.findViewById(R.id.loading_message)
            loadingMessage.text = loadingMessageText
        }
        setContentView(view)
    }
}