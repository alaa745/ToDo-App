package com.example.todoapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.model.NoteItem

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(noteItem: NoteItem)

    @Update
    suspend fun update(noteItem: NoteItem)

    @Query("SELECT * FROM Note")
     fun getAll(): LiveData<List<NoteItem>>

    @Query("SELECT * FROM Note WHERE noteDate = :noteDate")
     fun getNoteInDate(noteDate: String): LiveData<List<NoteItem?>>

    @Query("SELECT isDone FROM Note WHERE noteId = :noteId")
     suspend fun ifIsDone(noteId: Int): Boolean?
}