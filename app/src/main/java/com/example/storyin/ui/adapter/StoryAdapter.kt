package com.example.storyin.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyin.R
import com.example.storyin.domain.model.StoryItem
import com.example.storyin.ui.activity.DetailStoryActivity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    private val stories = mutableListOf<StoryItem>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newStories: List<StoryItem>) {
        stories.clear()
        stories.addAll(newStories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, DetailStoryActivity::class.java).apply {
                putExtra("STORY_ID", story.id)
            }
            val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                it.context as Activity,
                Pair(holder.imageView, "story"),
                Pair(holder.nameTextView, "name"),
                Pair(holder.createdAtTextView, "date")
            )
            it.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    override fun getItemCount() = stories.size

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_item_photo)
        val nameTextView: TextView = itemView.findViewById(R.id.tv_item_name)
        val createdAtTextView: TextView = itemView.findViewById(R.id.tv_created_at)

        fun bind(story: StoryItem) {
            nameTextView.text = story.name

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

            val parsedDate = inputFormat.parse(story.createdAt)
            createdAtTextView.text = parsedDate?.let { outputFormat.format(it) } ?: story.createdAt

            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(imageView)
        }
    }
}