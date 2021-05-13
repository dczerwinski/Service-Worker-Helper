package com.wat.serviceworkerhelper.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.viewmodel.GuidesViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class RateDialog(
    private val rating: Float,
    private val viewModel: GuidesViewModel,
    private val guide: Guide,
    private val isAdding: Boolean
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.alertDialog)
        val layoutInflater = requireActivity().layoutInflater
        val view = layoutInflater.inflate(R.layout.dialog_rate, null)

        val opinion = view?.findViewById<EditText>(R.id.opinion)
        val ratingBar = view?.findViewById<RatingBar>(R.id.ratingDialog)
        ratingBar!!.rating = rating
        val creatorUID = FirebaseAuth.getInstance().currentUser!!.uid

        val positiveButtonText: String
        if (isAdding) {
            positiveButtonText = getString(R.string.add)
        } else {
            positiveButtonText = getString(R.string.edit)
            opinion?.setText(guide.opinions[creatorUID]?.opinion)
        }

        val dialog = builder.setView(view)
            .setTitle(R.string.rateIt)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(positiveButtonText, null)
            .create()

        dialog.setOnShowListener {
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                if (ratingBar.rating > 0) {
                    val myOpinion = Guide.Opinion(
                        creatorUID,
                        opinion!!.text.toString(),
                        ratingBar.rating,
                        SimpleDateFormat("dd.MM.yyyy").format((Date()))
                    )
                    guide.opinions[creatorUID] = myOpinion
                    viewModel.changeOpinion(guide)
                    dialog.dismiss()
                    Toast.makeText(requireContext(), R.string.opinionAdded, Toast.LENGTH_SHORT)
                        .show()
                    Log.i(TAG, "rate added = $opinion")
                } else {
                    Toast.makeText(requireContext(), R.string.first_rate, Toast.LENGTH_SHORT).show()
                }
            }
        }

        return dialog
    }

    companion object {

        private const val TAG = "RateDialog"

        fun newInstance(
            rating: Float,
            viewModel: GuidesViewModel,
            guide: Guide,
            isAdding: Boolean
        ): RateDialog {
            return RateDialog(rating, viewModel, guide, isAdding)
        }
    }
}