package com.example.notesapp.network.repository

import com.example.notesapp.model.Note
import com.example.notesapp.model.User
import com.example.notesapp.util.UiState

interface NoteRepository {
    fun getNotes(user: User?, result: (UiState<List<Note>>) -> Unit)
    fun addNote(note: Note, result: (UiState<Pair<Note,String>>) -> Unit)
}