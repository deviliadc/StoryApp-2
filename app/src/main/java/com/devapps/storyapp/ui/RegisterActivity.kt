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
import com.devapps.storyapp.databinding.ActivityRegisterBinding
import com.devapps.storyapp.di.Injection
import com.devapps.storyapp.viewmodel.RegisterViewModel
import com.devapps.storyapp.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupViewModel()
        setupView()
        setupAction()
        setAnimation()
    }

    private fun setupViewModel() {
        val repository = Injection.provideRepository(this)
        registerViewModel = ViewModelProvider(this,
            ViewModelFactory(repository))[RegisterViewModel::class.java]

        registerViewModel.registerResult.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    showLoad(false)
                    Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
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

    private fun setupView() {
        registerViewModel.registerResult.observe(this) {
            when (it) {
                is Resource.Success -> {
                    showLoad(false)
                    Toast.makeText(this, it.data, Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                }
                is Resource.Loading -> showLoad(true)
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    showLoad(false)
                }

                else -> {

                }
            }
        }
    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            if (valid()) {
                val name = binding.nameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                registerViewModel.register(name, email, password)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.check_input),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun valid() =
        binding.emailEditText.error == null
                && binding.passwordEditText.error == null
                && binding.nameEditText.error == null
                && !binding.emailEditText.text.isNullOrEmpty()
                && !binding.passwordEditText.text.isNullOrEmpty()
                && !binding.nameEditText.text.isNullOrEmpty()

    private fun showLoad(isLoad: Boolean) {
        if (isLoad){
            binding.progressBar.visibility = View.VISIBLE
        }
        else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setAnimation() {
        val translateAnimator = ObjectAnimator.ofFloat(binding.registerImageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val tvTitle = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val tvName = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val etName = ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 1f).setDuration(100)
        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val etEmail = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(100)
        val tvPassword = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val etPassword = ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(100)
        val btnRegister = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(100)
        val msgLogin = ObjectAnimator.ofFloat(binding.massageLoginLink, View.ALPHA, 1f).setDuration(100)
        val linkLogin = ObjectAnimator.ofFloat(binding.loginLink, View.ALPHA, 1f).setDuration(100)

        val textAnim = AnimatorSet().apply {
            playTogether(tvTitle, msgLogin, linkLogin)
        }

        val layoutAnim = AnimatorSet().apply {
            playTogether(tvName, etName, tvEmail, etEmail, tvPassword, etPassword, btnRegister)
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