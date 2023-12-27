package com.example.app.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity (tableName = "liked_images")
data class LikedImage (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val image: String
)
