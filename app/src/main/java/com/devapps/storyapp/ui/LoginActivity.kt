package com.devapps.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.devapps.storyapp.R
import com.devapps.storyapp.data.Resource
import com.devapps.storyapp.databinding.ActivityLoginBinding
import com.devapps.storyapp.di.Injection
import com.devapps.storyapp.viewmodel.LoginViewModel
import com.devapps.storyapp.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setupView()
        setupAction()
        setAnimation()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            if (valid()) {
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                loginViewModel.login(email, password)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.check_input),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupView() {
        loginViewModel.loginResult.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    showLoad(false)
                    navigateToMainActivity()
                }
                is Resource.Loading -> showLoad(true)
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                    showLoad(false)
                }

                else -> {

                }
            }
        }
    }

    private fun setupViewModel() {
        val repository = Injection.provideRepository(this)
        loginViewModel = ViewModelProvider(this,
            ViewModelFactory(repository))[LoginViewModel::class.java]

        loginViewModel.loginResult.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    showLoad(false)
                    navigateToMainActivity()
                }
                is Resource.Loading -> showLoad(true)
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                    showLoad(false)
                }

                else -> {

                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoad(isLoad: Boolean) {
        if (isLoad){
            binding.progressBar.visibility = View.VISIBLE
        }
        else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun valid() =
        binding.emailEditText.error == null && binding.passwordEditText.error == null &&
                !binding.emailEditText.text.isNullOrEmpty() && !binding.passwordEditText.text.isNullOrEmpty()

    private fun setAnimation() {
        val translateAnimator = ObjectAnimator.ofFloat(binding.loginImageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val titleTextView = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val msgTextView = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val msgRegister = ObjectAnimator.ofFloat(binding.massageRegisterLink, View.ALPHA, 1f).setDuration(100)
        val linkRegister = ObjectAnimator.ofFloat(binding.registerLink, View.ALPHA, 1f).setDuration(100)
        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val etEmail = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(100)
        val tvPassword = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val etPassword = ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(100)
        val btnLogin = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)


        val textAnim = AnimatorSet().apply {
            playTogether(titleTextView, msgTextView, msgRegister, linkRegister)
        }

        val layoutAnim = AnimatorSet().apply {
            playTogether(tvEmail, etEmail, tvPassword, etPassword, btnLogin)
        }

        AnimatorSet().apply {
            playSequentially(
                translateAnimator,
                textAnim,
                layoutAnim
            )
            startDelay = 100
            start()
        }
    }
}
