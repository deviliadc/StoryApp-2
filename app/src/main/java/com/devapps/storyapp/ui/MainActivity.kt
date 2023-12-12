package com.devapps.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import com.devapps.storyapp.R
import com.devapps.storyapp.adapter.StoryAdapter
import com.devapps.storyapp.data.Resource
import com.devapps.storyapp.databinding.ActivityMainBinding
import com.devapps.storyapp.di.Injection
import com.devapps.storyapp.viewmodel.MainViewModel
import com.devapps.storyapp.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupToolbar()
        setupView()
        setupClickListeners()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_map -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                handleLogout()
                true
            }
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun handleLogout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.alert_title))
        builder.setMessage(getString(R.string.alert_message))
        builder.setNegativeButton(getString(R.string.alert_negativ)) { _, _ -> }
        builder.setPositiveButton(getString(R.string.alert_positive)) { _, _ ->
            mainViewModel.logoutResult.observe(this) { result ->
                when (result) {
                    is Resource.Success -> {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finishAffinity()
                    }
                    is Resource.Loading -> {
                        showLoad(true)
                    }
                    is Resource.Error -> {
                        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                        showLoad(false)
                    }

                    else -> {

                    }
                }
            }

            mainViewModel.sessions.observe(this) { token ->
                if (token.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        mainViewModel.logout()
                    }
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }

        val alert = builder.create()
        alert.show()
    }

    private fun setupViewModel() {
        val repository = Injection.provideRepository(applicationContext)
        val viewModelFactory = ViewModelFactory(repository)

        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    private fun setupView() {
        storyAdapter = StoryAdapter()
        mainViewModel.getSession()

        mainViewModel.sessions.observe(this) { userToken ->
            if (userToken.isNotEmpty()) {
                lifecycleScope.launch {

//                    storyAdapter.loadStateFlow.collectLatest { loadState ->
//                        when (loadState.refresh) {
//                            is LoadState.Loading -> showLoad(true)
//                            is LoadState.Error -> {
//                                showLoad(false)
//                                Toast.makeText(
//                                    this@MainActivity,
//                                    "Error loading stories",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                            is LoadState.NotLoading -> {
//                                showLoad(false)
//                                storyAdapter.refresh()
//                            }
//                        }
//                    }

                    mainViewModel.getStories(userToken).observe(this@MainActivity) { pagingData ->
                        storyAdapter.submitData(lifecycle, pagingData)
                    }
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        with(binding.rvStory) {
            setHasFixedSize(true)
            adapter = storyAdapter
        }
    }


    private fun showLoad(isLoad: Boolean) {
        if (isLoad){
            binding.progressBar.visibility = View.VISIBLE
        }
        else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        storyAdapter.submitData(lifecycle, PagingData.empty())
        mainViewModel.sessions.value?.let {
            mainViewModel.getStories(it).observe(this@MainActivity) { pagingData ->
                storyAdapter.submitData(lifecycle, pagingData)
            }
        }
    }
}
