package com.typesmart.keyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import android.view.inputmethod.InputConnection

class MyKeyboardService : InputMethodService() {
    private lateinit var aiActionPanel: AiActionPanel

    override fun onCreateInputView(): View {
        aiActionPanel = AiActionPanel(this)
        val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)
        bindAllKeys(keyboardView)
        return keyboardView
    }

    private fun bindAllKeys(root: View) {
        val letters = "qwertyuiopasdfghjklzxcvbnm"
        letters.forEach { ch ->
            val keyId = resources.getIdentifier("key_$ch", "id", packageName)
            root.findViewById<Button>(keyId)?.setOnClickListener {
                animatePress(it)
                commitText(ch.toString())
            }
        }

        root.findViewById<Button>(R.id.key_space).setOnClickListener {
            animatePress(it)
            commitText(" ")
        }

        root.findViewById<Button>(R.id.key_return).setOnClickListener {
            animatePress(it)
            currentInputConnection?.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
            )
            currentInputConnection?.sendKeyEvent(
                KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER)
            )
        }

        root.findViewById<Button>(R.id.key_backspace).setOnClickListener {
            animatePress(it)
            currentInputConnection?.deleteSurroundingText(1, 0)
        }

        root.findViewById<Button>(R.id.key_ai).setOnClickListener {
            animatePress(it)
            onAiPressed()
        }
    }

    private fun onAiPressed() {
        val inputConnection = currentInputConnection ?: return
        val selectedText = inputConnection.getSelectedText(0)?.toString().orEmpty()
        val textBeforeCursor = inputConnection.getTextBeforeCursor(500, 0)?.toString().orEmpty()

        val sourceText = if (selectedText.isNotBlank()) selectedText else textBeforeCursor
        if (sourceText.isBlank()) {
            Toast.makeText(this, "Type or select text first", Toast.LENGTH_SHORT).show()
            return
        }

        aiActionPanel.show(sourceText = sourceText) { aiResult ->
            replaceOriginalText(inputConnection, selectedText, sourceText, aiResult)
        }
    }

    private fun replaceOriginalText(
        inputConnection: InputConnection,
        selectedText: String,
        sourceText: String,
        aiResult: String
    ) {
        if (selectedText.isNotBlank()) {
            inputConnection.commitText(aiResult, 1)
            return
        }

        val deleteCount = sourceText.takeLast(500).length
        inputConnection.deleteSurroundingText(deleteCount, 0)
        inputConnection.commitText(aiResult, 1)
    }

    private fun commitText(value: String) {
        currentInputConnection?.commitText(value, 1)
    }

    private fun animatePress(view: View) {
        view.animate()
            .scaleX(0.94f)
            .scaleY(0.94f)
            .setDuration(55)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(55)
                    .start()
            }
            .start()
    }
}
