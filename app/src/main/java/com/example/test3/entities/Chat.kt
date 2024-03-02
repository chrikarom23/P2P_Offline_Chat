package com.example.test3.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Chat (
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val chatname: String
)