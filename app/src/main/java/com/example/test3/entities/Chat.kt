package com.example.test3.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Chat (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 111,
    val chatname: String
)