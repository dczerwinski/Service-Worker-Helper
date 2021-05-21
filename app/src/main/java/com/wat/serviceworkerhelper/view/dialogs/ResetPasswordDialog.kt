package com.wat.serviceworkerhelper.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.DialogResetPasswordBinding

class ResetPasswordDialog : AppCompatDialogFragment() {

    companion object {
        private const val TAG = "ResetPasswordDialog"
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity(), R.style.alertDialog)
        val binding = DialogResetPasswordBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        return builder.setView(binding.root)
            .setTitle(R.string.reset_password_dialog_title)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.ok) { _, _ ->
                val email = binding.emailET.text.toString()
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            Toast.makeText(
                                binding.root.context,
                                R.string.reset_email_send,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener {
                            Log.e(TAG, it.toString())
                            Toast.makeText(
                                binding.root.context,
                                R.string.something_went_wrong,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(
                        binding.root.context,
                        R.string.wrong_email,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.create()
    }
}