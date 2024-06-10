package com.example.test3

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.test3.entities.Chat
import com.example.test3.entities.Chat_line
import com.example.test3.entities.User
import com.example.test3.entities.relations.ChatwithChat_line

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat_line(chat_line:Chat_line)

    @Upsert
    suspend fun insertChat(chat:Chat)

    @Upsert
    suspend fun insertUser(user: User)

    @Query("DELETE FROM chat WHERE id = :cid")
    suspend fun delete_this_chat(cid: Int)

    @Query("DELETE FROM chat")
    suspend fun deletechats()

    @Query("DELETE FROM Chat_line")
    suspend fun deletechatdata()

    @Transaction
    @Query("SELECT * FROM Chat")
    suspend fun get_all_chatnames(): List<Chat>

    @Transaction
    @Query("SELECT * FROM User")
    suspend fun get_all_usernames(): List<User>

    @Transaction
    @Query("SELECT * FROM Chat_line Where cid = :cid")
    suspend fun get_chatlines_with_cid(cid: Int): List<Chat_line>

    @Transaction
    @Query("SELECT username FROM User Where uid = :uid")
    suspend fun get_username(uid: String): String

    @Transaction
    @Query("SELECT chatname FROM Chat INNER JOIN Chat_line Where cid = :cid")
    suspend fun get_chatname(cid: Int): String

    @Transaction
    @Query("SELECT timestamp FROM Chat_line INNER JOIN Chat Where cid = :cid")
    suspend fun get_timestamp(cid: Int): Long
    @Transaction
    @Query("SELECT * FROM Chat_line")
    suspend fun get_all_chatlines(): List<Chat_line>

}