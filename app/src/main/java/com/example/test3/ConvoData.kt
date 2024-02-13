package com.example.test3

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity
data class ConvoData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val deviceName :String,
    val timestamp: String
)
