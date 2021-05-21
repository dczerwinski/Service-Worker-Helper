package com.wat.serviceworkerhelper.view.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.DialogReportBinding
import com.wat.serviceworkerhelper.model.AppRoomDatabase
import com.wat.serviceworkerhelper.model.entities.Guide
import com.wat.serviceworkerhelper.model.entities.Report
import com.wat.serviceworkerhelper.model.repositories.GuideEntityRepository
import com.wat.serviceworkerhelper.model.repositories.ReportEntityRepository
import com.wat.serviceworkerhelper.view.dashboard.allguides.singleguide.SingleGuideActivity
import com.wat.serviceworkerhelper.viewmodel.GuidesViewModel
import com.wat.serviceworkerhelper.viewmodel.ReportsViewModel
import java.text.SimpleDateFormat
import java.util.*

class ReportDialog(
    private val guide: Guide,
    private val activity: SingleGuideActivity
) : DialogFragment() {

    companion object {
        fun newInstance(
            guide: Guide,
            activity: SingleGuideActivity
        ): ReportDialog {
            return ReportDialog(guide, activity)
        }
    }

    private val database by lazy { AppRoomDatabase.getDatabase(activity) }
    private val reportsRepository by lazy { ReportEntityRepository(database.reportsDao()) }
    private val guidesRepository by lazy { GuideEntityRepository(database.guideDao()) }
    private val reportsViewModel: ReportsViewModel by viewModels {
        ReportsViewModel.ReportsViewModelFactory(reportsRepository)
    }
    private val guidesViewModel: GuidesViewModel by viewModels {
        GuidesViewModel.GuidesViewModelFactory(guidesRepository)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext(), R.style.alertDialog)
        val binding = DialogReportBinding.inflate(layoutInflater)

        val dialog = builder.setView(binding.root)
            .setTitle(R.string.report_it)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.report, null)
            .create()

        dialog.setOnShowListener {
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener { view ->
                if (binding.reportContent.text.toString().isNotEmpty()) {
                    guide.guideStatus = Guide.Status.REPORTED
                    reportsViewModel.insert(
                        Report(
                            guide.uid,
                            FirebaseAuth.getInstance().currentUser!!.uid,
                            binding.reportContent.text.toString(),
                            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format((Date()))
                        )
                    )
                    guidesViewModel.update(guide)
                    Toast.makeText(
                        view.context,
                        R.string.guide_reported,
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.dismiss()
                    activity.finish()
                } else {
                    Toast.makeText(
                        view.context,
                        R.string.fill_all_fields,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return dialog
    }
}