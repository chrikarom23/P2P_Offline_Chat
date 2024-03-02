package com.example.test3

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.test3.entities.Chat
import com.example.test3.entities.Chat_line
import com.example.test3.entities.User

@Database(
    entities = [
        Chat::class,
        Chat_line::class,
        User::class
    ],
    version = 1
)
abstract class Chat_Database : RoomDatabase() {


    abstract val chatDao: ChatDao

    companion object{
        @Volatile
        private var INSTANCE: Chat_Database ?= null

        fun getInstance(context: Context) : Chat_Database{
            synchronized(this){
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    Chat_Database::class.java,
                    "chat_db").build().also { INSTANCE=it }
            }
        }
    }


}