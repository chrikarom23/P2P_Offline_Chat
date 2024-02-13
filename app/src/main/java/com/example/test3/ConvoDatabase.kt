package com.example.test3

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase


@Database(
    entities = [ConvoData::class],
    version = 1
)

abstract class ConvoDatabase: RoomDatabase() {
    abstract val convodao: ConvoDao


}