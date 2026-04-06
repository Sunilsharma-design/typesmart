package com.typesmart.keyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var btnEnable: Button
    private lateinit var btnSelectDefault: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvDone: TextView
    private lateinit var etTest: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnEnable = findViewById(R.id.btnEnable)
        btnSelectDefault = findViewById(R.id.btnSelectDefault)
        tvStatus = findViewById(R.id.tvStatus)
        tvDone = findViewById(R.id.tvDone)
        etTest = findViewById(R.id.etTest)

        btnEnable.setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        btnSelectDefault.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }
    }

    override fun onResume() {
        super.onResume()
        updateOnboardingState()
    }

    private fun updateOnboardingState() {
        val enabled = isKeyboardEnabled()
        val selected = isKeyboardSelected()

        when {
            selected -> {
                tvStatus.text = getString(R.string.onboarding_status_selected)
                tvDone.visibility = TextView.VISIBLE
                etTest.visibility = EditText.VISIBLE
            }
            enabled -> {
                tvStatus.text = getString(R.string.onboarding_status_enabled)
                tvDone.visibility = TextView.GONE
                etTest.visibility = EditText.GONE
            }
            else -> {
                tvStatus.text = ""
                tvDone.visibility = TextView.GONE
                etTest.visibility = EditText.GONE
            }
        }
    }

    private fun isKeyboardEnabled(): Boolean {
        val enabledInputMethods = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_INPUT_METHODS
        ).orEmpty()
        return enabledInputMethods.contains(packageName)
    }

    private fun isKeyboardSelected(): Boolean {
        val defaultMethod = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        ).orEmpty()
        return defaultMethod.contains(packageName)
    }
}
