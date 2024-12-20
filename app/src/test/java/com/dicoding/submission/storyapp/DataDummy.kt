package com.dicoding.submission.storyapp

import com.dicoding.submission.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val storyItem = ListStoryItem(
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-$i-.png",
                description = "Description $i",
                id = "$i",
                name = "Name $i",
                lon = "100.35$i".toDouble(),
                createdAt = "2024-05-08T06:$i:18.598Z",
                lat = "-0.93$i".toDouble(),
            )
            items.add(storyItem)
        }
        return items
    }
}
