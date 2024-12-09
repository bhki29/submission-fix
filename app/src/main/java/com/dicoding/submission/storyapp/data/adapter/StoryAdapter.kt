package com.dicoding.submission.storyapp.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.submission.storyapp.R
import com.dicoding.submission.storyapp.data.response.ListStoryItem

class StoryAdapter(
    private val stories: List<ListStoryItem>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)
        holder.itemView.setOnClickListener {
            story.id?.let { id -> onItemClick(id) }
        }
    }

    override fun getItemCount(): Int = stories.size

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tv_description)
        private val imageView: ImageView = itemView.findViewById(R.id.img_story)

        fun bind(story: ListStoryItem) {
            titleTextView.text = story.name
            descriptionTextView.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(imageView)
        }
    }
}

