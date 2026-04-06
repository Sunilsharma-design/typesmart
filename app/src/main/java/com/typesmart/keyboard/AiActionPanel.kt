package com.typesmart.keyboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.concurrent.Executors

class AiActionPanel(private val context: Context) {
    private val worker = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun show(
        sourceText: String,
        onReplace: (resultText: String) -> Unit
    ) {
        val dialog = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.ai_action_panel, null)
        dialog.setContentView(view)

        val actionGrid = view.findViewById<GridLayout>(R.id.actionGrid)
        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val tvResult = view.findViewById<TextView>(R.id.tvResult)
        val resultActions = view.findViewById<View>(R.id.resultActions)
        val btnCopy = view.findViewById<Button>(R.id.btnCopy)
        val btnReplace = view.findViewById<Button>(R.id.btnReplace)

        var aiResult = ""

        fun runAction(action: String) {
            actionGrid.isEnabled = false
            setActionButtonsEnabled(view, false)
            progress.visibility = View.VISIBLE
            tvResult.visibility = View.GONE
            resultActions.visibility = View.GONE

            worker.execute {
                val result = ApiClient.rewriteText(sourceText, action)
                mainHandler.post {
                    progress.visibility = View.GONE
                    actionGrid.isEnabled = true
                    setActionButtonsEnabled(view, true)

                    result.fold(
                        onSuccess = { rewritten ->
                            aiResult = rewritten
                            tvResult.text = rewritten
                            tvResult.visibility = View.VISIBLE
                            resultActions.visibility = View.VISIBLE
                        },
                        onFailure = { err ->
                            Toast.makeText(
                                context,
                                err.message ?: "Failed to generate text",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }

        view.findViewById<Button>(R.id.btnRewrite).setOnClickListener { runAction("rewrite") }
        view.findViewById<Button>(R.id.btnProfessional).setOnClickListener { runAction("professional") }
        view.findViewById<Button>(R.id.btnCasual).setOnClickListener { runAction("casual") }
        view.findViewById<Button>(R.id.btnTranslate).setOnClickListener { runAction("translate_en") }
        view.findViewById<Button>(R.id.btnFunny).setOnClickListener { runAction("funny") }
        view.findViewById<Button>(R.id.btnFlirty).setOnClickListener { runAction("flirty") }

        btnCopy.setOnClickListener {
            if (aiResult.isBlank()) return@setOnClickListener
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("AI Result", aiResult))
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
        }

        btnReplace.setOnClickListener {
            if (aiResult.isBlank()) return@setOnClickListener
            onReplace(aiResult)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setActionButtonsEnabled(view: View, enabled: Boolean) {
        val actionIds = listOf(
            R.id.btnRewrite,
            R.id.btnProfessional,
            R.id.btnCasual,
            R.id.btnTranslate,
            R.id.btnFunny,
            R.id.btnFlirty
        )
        actionIds.forEach { id ->
            view.findViewById<Button>(id).isEnabled = enabled
        }
    }
}
