package com.example.storyin.widget

import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.example.storyin.R
import com.example.storyin.data.remote.api.ApiConfig
import com.example.storyin.data.preferences.UserPreference
import com.example.storyin.domain.model.StoryItem
import com.example.storyin.data.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class StoryRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private var storyList = mutableListOf<StoryItem>()
    private lateinit var storyRepository: StoryRepository
    private lateinit var userPreference: UserPreference

    override fun onCreate() {
        userPreference = UserPreference.getInstance(context)
        storyRepository = StoryRepository.getInstance(
            ApiConfig.getApiService(),
            userPreference
        )
    }

    override fun onDataSetChanged() {
        runBlocking {
            try {
                val response = storyRepository.getStories(size = 5)
                storyList.clear()
                storyList.addAll(response.listStory)
            } catch (e: Exception) {
                e.printStackTrace()
                storyList.clear()
            }
        }
    }

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item_story)

        if (position < storyList.size) {
            try {
                val story = storyList[position]
                val bitmap: Bitmap? = runBlocking {
                    try {
                        withContext(Dispatchers.IO) {
                            Glide.with(context)
                                .asBitmap()
                                .load(story.photoUrl)
                                .submit()
                                .get()
                        }
                    } catch (e: Exception) {
                        null
                    }
                }

                bitmap?.let {
                    rv.setImageViewBitmap(R.id.widget_image, it)
                }

                rv.setTextViewText(R.id.widget_name, story.name)
                rv.setTextViewText(R.id.widget_description, story.description)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return rv
    }

    override fun getCount(): Int = storyList.size
    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(i: Int): Long = i.toLong()
    override fun hasStableIds(): Boolean = false
    override fun onDestroy() {}
}