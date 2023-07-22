package com.example.notesapp.util

import com.example.notesapp.model.Note

interface NotesClickListener {

    fun onClickListener(note: Note)
}