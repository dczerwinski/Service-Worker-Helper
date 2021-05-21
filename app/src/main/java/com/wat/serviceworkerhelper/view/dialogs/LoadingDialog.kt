package com.wat.serviceworkerhelper.view.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import androidx.annotation.StyleRes
import com.wat.serviceworkerhelper.databinding.DialogLoadingBinding

class LoadingDialog(
    context: Context,
    @StyleRes themeResId: Int,
    private val loadingMessageText: String = ""
) : Dialog(context, themeResId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val binding = DialogLoadingBinding.inflate(layoutInflater)
        if (loadingMessageText.isNotEmpty()) {
            binding.loadingMessage.text = loadingMessageText
        }
        setContentView(binding.root)
    }
}