package com.example.test3.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey(autoGenerate = false)
    //its just Youuu in Hex
    val uid: String,
    val username: String
)