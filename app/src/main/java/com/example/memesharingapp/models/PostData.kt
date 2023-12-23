package com.example.memesharingapp.models

data class PostData(
    val subreddit: String,
    val title: String,
    val url: String,
    val author: String,
    val ups: Int
)
