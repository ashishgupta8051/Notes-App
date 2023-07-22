package com.example.notesapp.network.module

import android.content.SharedPreferences
import com.example.notesapp.network.repository.NoteRepositoryImp
import com.example.notesapp.network.repository.AuthRepository
import com.example.notesapp.network.repository.AuthRepositoryImp
import com.example.notesapp.network.repository.NoteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAutghRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImp(auth,database,appPreferences,gson)
    }


    @Provides
    @Singleton
    fun provideNoteRepository(
        database: FirebaseFirestore,
        storageRef:StorageReference
    ): NoteRepository {
        return NoteRepositoryImp(database, storageRef)
    }

}