package com.example.storyin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyin.R
import com.example.storyin.ui.viewmodel.RegisterViewModel
import com.example.storyin.ui.viewmodel.factory.AuthViewModelFactory
import com.google.android.material.button.MaterialButton

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewModel: RegisterViewModel

    private lateinit var btnBack: ImageButton
    private lateinit var btnSignUp: MaterialButton
    private lateinit var edRegisterName: EditText
    private lateinit var edRegisterEmail: EditText
    private lateinit var edRegisterPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var progressBarLoading: ProgressBar
    private lateinit var formContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnBack = findViewById(R.id.btnBack)
        btnSignUp = findViewById(R.id.btnSignUp)
        edRegisterName = findViewById(R.id.ed_register_name)
        edRegisterEmail = findViewById(R.id.ed_register_email)
        edRegisterPassword = findViewById(R.id.ed_register_password)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        progressBarLoading = findViewById(R.id.progressBarLoading)
        formContainer = findViewById(R.id.formContainer)

        progressBarLoading.visibility = View.GONE
        setupViewModel()
        setupAction()
        observeViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory.getInstance(this)
        )[RegisterViewModel::class.java]
    }

    private fun setupAction() {
        btnBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSignUp.setOnClickListener {
            val name = edRegisterName.text.toString()
            val email = edRegisterEmail.text.toString()
            val password = edRegisterPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()

            when {
                name.isEmpty() -> {
                    edRegisterName.error = getString(R.string.name_required)
                    edRegisterName.requestFocus()
                }

                email.isEmpty() -> {
                    edRegisterEmail.error = getString(R.string.email_required)
                    edRegisterEmail.requestFocus()
                }

                password.isEmpty() -> {
                    edRegisterPassword.error = getString(R.string.password_required)
                    edRegisterPassword.requestFocus()
                }

                password.length < 8 -> {
                    edRegisterPassword.error = getString(R.string.password_min_length)
                    edRegisterPassword.requestFocus()
                }

                password != confirmPassword -> {
                    editTextConfirmPassword.error = getString(R.string.password_mismatch)
                    editTextConfirmPassword.requestFocus()
                }

                else -> {
                    showLoading(true)
                    viewModel.register(name, email, password)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.registrationResult.observe(this) { result ->
            result.fold(
                onSuccess = { response ->
                    showLoading(false)
                    if (!response.error) {
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.register_successful),
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onFailure = { exception ->
                    showLoading(false)
                    Toast.makeText(
                        this@RegisterActivity,
                        exception.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBarLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSignUp.isEnabled = !isLoading
        edRegisterName.isEnabled = !isLoading
        edRegisterEmail.isEnabled = !isLoading
        edRegisterPassword.isEnabled = !isLoading
        editTextConfirmPassword.isEnabled = !isLoading
    }
}