package com.example.test3
import java.io.Serializable
import java.sql.Timestamp

data class message(val cname: String,var uname: String,val line_text: String, val timestampp: Long = java.util.Date().time)//{
//    val cname = cname;
//    val uname = uname;
//    val line_text = line_text;
//    val timestampp = timestampp;
//}