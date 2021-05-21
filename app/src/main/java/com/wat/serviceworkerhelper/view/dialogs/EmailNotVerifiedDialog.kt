package com.wat.serviceworkerhelper.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.firebase.auth.FirebaseUser
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.DialogEmailNotVerifiedBinding

class EmailNotVerifiedDialog(
    private val user: FirebaseUser
) : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity(), R.style.alertDialog)
        val binding = DialogEmailNotVerifiedBinding.inflate(layoutInflater)

        return builder.setView(binding.root)
            .setTitle(R.string.dialogTitle)
            .setNegativeButton(R.string.ok, null)
            .setPositiveButton(R.string.send_verification_email_again) { _, _ ->
                user.sendEmailVerification()
                Toast.makeText(context, R.string.email_send, Toast.LENGTH_SHORT).show()
            }.create()
    }
}