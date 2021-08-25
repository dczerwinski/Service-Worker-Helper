package com.wat.serviceworkerhelper.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.DialogChangePasswordBinding

class ChangePasswordDialog(
    private val user: FirebaseUser
) : AppCompatDialogFragment() {

    companion object {
        private const val TAG = "ChangePasswordDialog"
    }

    private lateinit var binding: DialogChangePasswordBinding
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogChangePasswordBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(binding.root.context, R.style.LoadingDialog)
        val builder = AlertDialog.Builder(activity, R.style.alertDialog).apply {
            setView(binding.root)
            setTitle(R.string.change_password)
            setNegativeButton(R.string.cancel, null)
            setPositiveButton(R.string.save, null)
        }

        val dialog = builder.create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                loadingDialog.show()
                val newPassword = binding.newPassword.text.toString()
                val reNewPassword = binding.reNewPassword.text.toString()
                val currentPassword = binding.currentPassword.text.toString()
                val credential: AuthCredential = EmailAuthProvider
                    .getCredential(user.email!!, currentPassword)

                user
                    .reauthenticate(credential)
                    .addOnSuccessListener {
                        if (newPassword.isEmpty() || reNewPassword.isEmpty()) {
                            loadingDialog.dismiss()
                            Toast
                                .makeText(
                                    context,
                                    R.string.fill_all_fields,
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        } else if (newPassword == reNewPassword) {
                            user
                                .updatePassword(newPassword)
                                .addOnSuccessListener {
                                    loadingDialog.dismiss()
                                    Toast
                                        .makeText(
                                            context,
                                            R.string.password_changed,
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                    dialog.dismiss()
                                }
                                .addOnFailureListener {
                                    loadingDialog.dismiss()
                                    Log.e(TAG, "error", it)
                                    if (it is FirebaseAuthWeakPasswordException) {
                                        Toast
                                            .makeText(
                                                context,
                                                R.string.too_weak_password,
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    } else {
                                        Toast
                                            .makeText(
                                                context,
                                                R.string.something_went_wrong,
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }
                        } else {
                            loadingDialog.dismiss()
                            Toast
                                .makeText(
                                    context,
                                    R.string.different_passwords,
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
                    .addOnFailureListener {
                        loadingDialog.dismiss()
                        Log.e(TAG, "re-authentication failed!", it)
                        Toast
                            .makeText(
                                context,
                                R.string.wrong_current_password,
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
            }
        }

        return dialog
    }
}