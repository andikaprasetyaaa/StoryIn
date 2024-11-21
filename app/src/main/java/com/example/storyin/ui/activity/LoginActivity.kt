package com.example.storyin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyin.R
import com.example.storyin.ui.viewmodel.LoginViewModel
import com.example.storyin.ui.viewmodel.factory.AuthViewModelFactory
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var loginButton: MaterialButton
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupViewModel()
        initViews()
        setupAction()
        observeViewModel()
        checkSession()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            AuthViewModelFactory.getInstance(this)
        )[LoginViewModel::class.java]
    }

    private fun initViews() {
        emailEditText = findViewById(R.id.ed_login_email)
        passwordEditText = findViewById(R.id.ed_login_password)
        progressBar = findViewById(R.id.progressBarLoading)
        loginButton = findViewById(R.id.btnLogin)
        backButton = findViewById(R.id.btnBack)

        progressBar.visibility = View.GONE
    }

    private fun checkSession() {
        viewModel.token.observe(this) { token ->
            if (token.isNotEmpty()) {
                navigateToHome()
            }
        }
    }

    private fun setupAction() {
        backButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            when {
                email.isEmpty() -> {
                    emailEditText.error = getString(R.string.email_required)
                    emailEditText.requestFocus()
                }

                password.isEmpty() -> {
                    passwordEditText.error = getString(R.string.password_required)
                    passwordEditText.requestFocus()
                }

                password.length < 8 -> {
                    passwordEditText.error = getString(R.string.password_min_length)
                    passwordEditText.requestFocus()
                }

                else -> {
                    showLoading(true)
                    viewModel.login(email, password)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.loginResult.observe(this) { result ->
            result.fold(
                onSuccess = { response ->
                    showLoading(false)

                    if (!response.error) {
                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.login_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToHome()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onFailure = { exception ->
                    showLoading(false)

                    Toast.makeText(
                        this@LoginActivity,
                        exception.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        loginButton.isEnabled = !isLoading
    }

    private fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}