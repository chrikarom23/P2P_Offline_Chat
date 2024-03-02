package com.example.test3.entities.relations

import androidx.room.Embedded
import androidx.room.ForeignKey
import androidx.room.Relation
import com.example.test3.entities.Chat_line
import com.example.test3.entities.Chat

data class ChatwithChat_line (
    @Embedded val chat: Chat,
    @Relation(
        parentColumn = "id",
        entityColumn = "cid"
    )
    val chat_lines: List<Chat_line>,
)
