package com.example.test3.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigInteger
import java.sql.Timestamp

@Entity(
    foreignKeys = [
        ForeignKey(entity = Chat::class, parentColumns = ["id"], childColumns = ["cid"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
        ForeignKey(entity = User::class, parentColumns = ["uid"], childColumns = ["uid"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
]
)
data class Chat_line(
    @PrimaryKey(autoGenerate = true)
    val chat_line_id: Int = 0,
    val line_text: String,
    val timestamp: Long = java.util.Date().time,
    val cid: Int,
    val uid: String="You"
)
