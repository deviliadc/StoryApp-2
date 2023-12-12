package com.devapps.storyapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.devapps.storyapp.data.model.Story
import com.devapps.storyapp.databinding.ActivityDetailStoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
        private const val DISPLAY_DATE_TIME_FORMAT = "dd MMMM yyyy HH:mm"
        private const val TIME_ZONE_ID = "GMT+7"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater )
        setContentView(binding.root)
        supportActionBar?.hide()
        setupView()
    }

    private fun setupView() {
        val detail = intent.getParcelableExtra<Story>(EXTRA_DETAIL)

        binding.apply {
            tvNameDetail.text = detail?.name
            tvDesc.text = detail?.description
            tvDate.text = formatDate(detail?.createdAt)
        }
        Glide.with(this)
            .load(detail?.photoUrl)
            .into(binding.imgStoryDetail)
    }

    private fun formatDate(dateString: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("id", "ID"))
            inputFormat.timeZone = TimeZone.getTimeZone(TIME_ZONE_ID)

            val outputFormat = SimpleDateFormat(DISPLAY_DATE_TIME_FORMAT, Locale("id", "ID"))
            outputFormat.timeZone = TimeZone.getTimeZone(TIME_ZONE_ID)

            val date = inputFormat.parse(dateString ?: "")
            outputFormat.format(date as Date)
        } catch (e: Exception) {
            ""
        }
    }
}