package com.example.storyin.ui.activity

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyin.R
import com.example.storyin.data.preferences.UserPreference
import com.example.storyin.data.remote.api.ApiConfig
import com.example.storyin.domain.model.StoryItem
import com.example.storyin.data.repository.StoryRepository
import com.example.storyin.ui.viewmodel.DetailStoryViewModel
import com.example.storyin.ui.viewmodel.factory.DetailStoryViewModelFactory

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var ivDetailPhoto: ImageView
    private lateinit var tvDetailName: TextView
    private lateinit var tvCreatedAt: TextView
    private lateinit var tvDetailDescription: TextView
    private lateinit var backButton: ImageButton

    private val detailStoryViewModel: DetailStoryViewModel by viewModels {
        DetailStoryViewModelFactory(
            StoryRepository.getInstance(
                ApiConfig.getApiService(),
                UserPreference.getInstance(this)
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_story)

        backButton = findViewById(R.id.btnBack)
        ivDetailPhoto = findViewById(R.id.iv_detail_photo)
        tvDetailName = findViewById(R.id.tv_detail_name)
        tvCreatedAt = findViewById(R.id.tv_created_at)
        tvDetailDescription = findViewById(R.id.tv_detail_description)

        val storyId = intent.getStringExtra("STORY_ID") ?: return

        detailStoryViewModel.loadStoryDetail(storyId)

        detailStoryViewModel.detailStory.observe(this) { detailStoryResponse ->
            updateUI(detailStoryResponse.story)
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun updateUI(story: StoryItem?) {
        story?.let {
            tvDetailName.text = it.name
            tvCreatedAt.text = it.createdAt
            tvDetailDescription.text = it.description
            Glide.with(this).load(it.photoUrl).into(ivDetailPhoto)
        }
    }
}