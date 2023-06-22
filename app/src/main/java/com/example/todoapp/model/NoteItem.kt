package com.example.todoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Note")
data class NoteItem(
    @PrimaryKey(autoGenerate = true)
    val noteId: Int = 0,
    val noteTitle: String,
    val noteDate: String,
    val noteTime: String,
    val isDone: Boolean
)
