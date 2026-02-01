package com.airport.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.airport.android.AirportApplication
import com.airport.android.api.RetrofitClient
import com.airport.android.databinding.ActivityLoginBinding
import com.airport.android.model.LoginRequest
import com.airport.android.model.RegisterRequest
import kotlinx.coroutines.launch

/**
 * Login/Register screen
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val sessionManager by lazy { (application as AirportApplication).sessionManager }
    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            goToMain()
            return
        }

        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            // Toggle between login and register
            tvToggleMode.setOnClickListener {
                isLoginMode = !isLoginMode
                updateMode()
            }

            // Login/Register button
            btnSubmit.setOnClickListener {
                if (isLoginMode) {
                    performLogin()
                } else {
                    performRegister()
                }
            }

            // Quick login with test account
            tvTestAccount.setOnClickListener {
                etEmail.setText("john@example.com")
                etPassword.setText("password123")
            }
        }
    }

    private fun updateMode() {
        binding.apply {
            if (isLoginMode) {
                tvTitle.text = "Welcome Back"
                tvSubtitle.text = "Sign in to continue"
                tilName.visibility = View.GONE
                tilPhone.visibility = View.GONE
                btnSubmit.text = "Sign In"
                tvToggleMode.text = "Don't have an account? Register"
            } else {
                tvTitle.text = "Create Account"
                tvSubtitle.text = "Sign up to get started"
                tilName.visibility = View.VISIBLE
                tilPhone.visibility = View.VISIBLE
                btnSubmit.text = "Register"
                tvToggleMode.text = "Already have an account? Sign In"
            }
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApi().login(LoginRequest(email, password))

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    sessionManager.saveToken(loginResponse.token)
                    sessionManager.saveUser(loginResponse.user)

                    Toast.makeText(this@LoginActivity, "Welcome ${loginResponse.user.name}!", Toast.LENGTH_SHORT).show()
                    goToMain()
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Connection error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun performRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getApi().register(
                    RegisterRequest(name, email, password, phone.ifEmpty { null })
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Account created! Please sign in.", Toast.LENGTH_SHORT).show()
                    isLoginMode = true
                    updateMode()
                } else {
                    Toast.makeText(this@LoginActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Connection error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            btnSubmit.isEnabled = !loading
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
