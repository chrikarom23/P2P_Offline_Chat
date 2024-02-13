package com.example.test3

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ConvoDao {

    @Upsert
    fun upsertConvo(convo: ConvoData)

    @Delete
    fun deleteConvo(convo: ConvoData)

    @Query("SELECT * FROM convodata ORDER BY timestamp DESC")
    fun getConvosByTimestamp(): List<ConvoData>

    @Query("SELECT * FROM convodata Where deviceName LIKE :deviceName LIMIT 1")
    fun getConvosByName(deviceName: String): List<ConvoData>

}