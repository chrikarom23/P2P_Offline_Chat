package com.example.test3

import java.sql.Timestamp

data class ConvoState(
    val convo: List<Convo> = emptyList(),
    val deviceName: String = "",
    val TimeStamp: String,
    val isDeletingConvo: Boolean = false,
    val sortType: sortType = com.example.test3.sortType.Timestamp
)
