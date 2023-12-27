package com.example.app.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LikedImageDao{
    @Insert
    suspend fun insert(likedImage: LikedImage)

    @Query("SELECT * FROM liked_images")
    suspend fun getAllLikedImages(): List<LikedImage>

    @Query("DELETE FROM liked_images")
    suspend fun clearTable()
}