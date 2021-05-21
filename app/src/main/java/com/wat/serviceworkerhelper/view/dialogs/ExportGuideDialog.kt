package com.wat.serviceworkerhelper.view.dialogs

import android.app.Activity
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wat.serviceworkerhelper.R
import com.wat.serviceworkerhelper.databinding.DialogExportGuideBinding
import com.wat.serviceworkerhelper.model.entities.Guide
import java.io.File

class ExportGuideDialog(
    private val guide: Guide,
    private val toPrintView: View,
    private val activity: Activity
) : BottomSheetDialogFragment() {

    companion object {
        private const val TAG = "ExportGuideDialog"
        fun newInstance(
            guide: Guide,
            toPrintView: View,
            activity: Activity
        ): ExportGuideDialog {
            return ExportGuideDialog(guide, toPrintView, activity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogExportGuideBinding.inflate(inflater, container, false)

        binding.layoutExportToPdf.setOnClickListener {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(
                toPrintView.measuredWidth,
                toPrintView.measuredHeight,
                1
            ).create()
            val page = document.startPage(pageInfo)
            toPrintView.draw(page.canvas)
            document.finishPage(page)

            val filePath = File(activity.filesDir, "external_files")
            filePath.mkdir()
            val file = File(filePath.path, "${guide.title}.pdf")
            try {
                document.writeTo(file.outputStream())
                document.close()
            } catch (e: Exception) {
                Log.e(TAG, "error", e)
            }

            val uri = FileProvider.getUriForFile(
                activity,
                activity.packageName + ".provider",
                file
            )
            activity.grantUriPermission(
                activity.applicationContext.packageName + ".provider",
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Log.i(TAG, "opening pdf, uri = $uri")
            val intent = ShareCompat.IntentBuilder.from(activity)
                .setStream(uri)
                .setType("application/pdf")
                .intent
                .setAction(Intent.ACTION_VIEW)
                .setDataAndType(uri, "application/pdf")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(intent)
        }

        return binding.root
    }
}