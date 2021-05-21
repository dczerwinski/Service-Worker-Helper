package com.wat.serviceworkerhelper.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.DialogRateBinding
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.viewmodel.GuidesViewModel
import java.text.SimpleDateFormat
import java.util.*

class RateDialog(
    private val rating: Float,
    private val viewModel: GuidesViewModel,
    private val guide: Guide,
    private val isAdding: Boolean
) : DialogFragment() {

    companion object {
        fun newInstance(
            rating: Float,
            viewModel: GuidesViewModel,
            guide: Guide,
            isAdding: Boolean
        ): RateDialog {
            return RateDialog(rating, viewModel, guide, isAdding)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.alertDialog)
        val binding = DialogRateBinding.inflate(layoutInflater)

        binding.ratingBar.rating = rating
        val creatorUID = FirebaseAuth.getInstance().currentUser!!.uid

        val positiveButtonText: String
        if (isAdding) {
            positiveButtonText = getString(R.string.add)
        } else {
            positiveButtonText = getString(R.string.edit)
            binding.opinion.setText(guide.opinions[creatorUID]?.opinion)
        }

        val dialog = builder.setView(binding.root)
            .setTitle(R.string.rateIt)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(positiveButtonText, null)
            .create()

        dialog.setOnShowListener {
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                if (binding.ratingBar.rating > 0) {
                    val myOpinion = Guide.Opinion(
                        creatorUID,
                        binding.opinion.text.toString(),
                        binding.ratingBar.rating,
                        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format((Date()))
                    )
                    guide.opinions[creatorUID] = myOpinion
                    viewModel.changeOpinion(guide)
                    dialog.dismiss()
                    Toast.makeText(requireContext(), R.string.opinionAdded, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), R.string.first_rate, Toast.LENGTH_SHORT).show()
                }
            }
        }

        return dialog
    }
}