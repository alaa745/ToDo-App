package com.example.todoapp

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.database.AppDatabase
import com.example.todoapp.model.Day
import com.example.todoapp.model.NoteItem
import com.example.todoapp.repository.NoteRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class NoteViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var noteList: LiveData<List<NoteItem?>>
    var allNotes: LiveData<List<NoteItem>>
    private val _date = MutableLiveData<String>()
    var date: LiveData<String> = _date

    private val _isDone = MutableLiveData(false)
    var isDone: LiveData<Boolean?> = _isDone

    private val repository: NoteRepository

    init {
        val databaseDao = AppDatabase.getInstance(application).noteDao()
        repository = NoteRepository(databaseDao)
        allNotes = repository.allNotes
        _date.value = generateUpcomingDays()
    }

    fun updateDate(newDate: String){
        _date.value = newDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateUpcomingDays(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = LocalDate.now()
        val date = currentDate.format(formatter)

        return date
    }

    fun addNote(noteItem: NoteItem) {
        viewModelScope.launch {
            repository.addNote(noteItem)
        }
    }

    fun updateNote(noteItem: NoteItem) {
        Log.d("updatee" , noteItem.isDone.toString())
        viewModelScope.launch {
            repository.updateNote(noteItem)
//            _isDone.value = noteItem.isDone
        }
    }

//    fun getIsDone(noteItem: NoteItem) {
//        viewModelScope.launch {
//            val bool = repository.getIsDone(noteItem.noteId)
//            _isDone.value = bool
//            Log.d("bolmodel" , bool.toString())
//            updateNote(noteItem)
//        }
//    }

    fun getNotesInDate(noteDate: String) {
        viewModelScope.launch {
            noteList = repository.getNotesInDate(noteDate)
        }
    }
}

class NoteViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}