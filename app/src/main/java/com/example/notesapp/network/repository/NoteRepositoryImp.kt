package com.example.notesapp.network.repository


import com.example.notesapp.model.Note
import com.example.notesapp.model.User
import com.example.notesapp.util.FireStoreCollection
import com.example.notesapp.util.FireStoreDocumentField
import com.example.notesapp.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class NoteRepositoryImp(val database: FirebaseFirestore) : NoteRepository {

    override fun getNotes(user: User?, result: (UiState<List<Note>>) -> Unit) {
        database.collection(FireStoreCollection.NOTE)
            .whereEqualTo(FireStoreDocumentField.USER_ID,user?.id)
            .get()
            .addOnSuccessListener {
                val notes = arrayListOf<Note>()
                for (document in it) {
                    val note = document.toObject(Note::class.java)
                    notes.add(note)
                }
                notes.reversed()
                result.invoke(
                    UiState.Success(notes)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun addNote(note: Note, result: (UiState<Pair<Note,String>>) -> Unit) {
        val document = database.collection(FireStoreCollection.NOTE).document()
        note.id = document.id
        document
            .set(note)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(Pair(note,"Note has been created successfully"))
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

}