package com.devapps.storyapp.viewmodel

import com.devapps.storyapp.data.model.Story

object DataDummy {

    fun generateDummyStory(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                id = "id",
                name = "name",
                description = "description",
                photoUrl = "photoUrl",
                createdAt = "createdAt",
                lon = 0.0,
                lat = 0.0
            )
            items.add(story)
        }
        return items
    }
}