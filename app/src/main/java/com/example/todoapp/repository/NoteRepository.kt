package com.example.todoapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.todoapp.database.NoteDao
import com.example.todoapp.model.NoteItem

class NoteRepository(val noteDao: NoteDao) {
    var allNotes = noteDao.getAll()

    lateinit var notes: LiveData<List<NoteItem?>>
    var isDone: Boolean? = false


    suspend fun addNote(noteItem: NoteItem) {
        noteDao.insert(noteItem)
    }

    suspend fun updateNote(noteItem: NoteItem) {
        Log.d("update2", noteItem.isDone.toString())
        noteDao.update(noteItem)
    }

    suspend fun getNotesInDate(noteDate: String): LiveData<List<NoteItem?>> {
        notes = noteDao.getNoteInDate(noteDate)

        return notes
    }

    suspend fun getIsDone(noteId: Int): Boolean? {
        isDone = noteDao.ifIsDone(noteId)

        return isDone
    }
}