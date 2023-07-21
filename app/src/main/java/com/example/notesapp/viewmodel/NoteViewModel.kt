package com.example.notesapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notesapp.model.Note
import com.example.notesapp.model.User
import com.example.notesapp.network.repository.NoteRepository
import com.example.notesapp.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(val repository: NoteRepository): ViewModel() {

    private val _notes = MutableLiveData<UiState<List<Note>>>()
    val note: LiveData<UiState<List<Note>>>
            get() = _notes

    private val _addNote = MutableLiveData<UiState<Pair<Note,String>>>()
    val addNote: LiveData<UiState<Pair<Note,String>>>
        get() = _addNote


    fun getNotes(user: User?) {
        _notes.value = UiState.Loading
        repository.getNotes(user) { _notes.value = it }
    }

    fun addNote(note: Note){
        _addNote.value = UiState.Loading
        repository.addNote(note) { _addNote.value = it }
    }

}