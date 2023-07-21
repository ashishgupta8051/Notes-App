package com.example.notesapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.R
import com.example.notesapp.adapter.NotesAdapter
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.ui.fragment.NotesBottomSheetFragment
import com.example.notesapp.model.Note
import com.example.notesapp.util.UiState
import com.example.notesapp.util.showShortToast
import com.example.notesapp.viewmodel.AuthViewModel
import com.example.notesapp.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val notesViewModel: NoteViewModel by viewModels()
    lateinit var notesAdapter: NotesAdapter
    var noteList:MutableList<Note> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvLogout.setOnClickListener(this)
        binding.btnAddNotes.setOnClickListener(this)


        notesAdapter = NotesAdapter(noteList)
        binding.noteListing.layoutManager = LinearLayoutManager(this)
        binding.noteListing.adapter = notesAdapter

        authViewModel.getSession {
            notesViewModel.getNotes(it)
        }

        observer()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observer() {
        notesViewModel.note.observe(this){state ->
            when(state){
                is UiState.Loading -> {
                    showProgress()
                }
                is UiState.Failure -> {
                    hideProgress()
                    state.error?.let { showShortToast(this, it) }
                }
                is UiState.Success -> {
                    hideProgress()
                    noteList.addAll(state.data.toMutableList())
                    notesAdapter.notifyDataSetChanged()
                }

                else -> {}
            }
        }
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when(view.id){
                R.id.tvLogout->{
                    authViewModel.logout {
                        startActivity(Intent(this, LoginRegisterActivity::class.java))
                        finish()
                    }
                } R.id.btnAddNotes->{
                val dialog = NotesBottomSheetFragment()
                dialog.getActivity(this)
                dialog.show(supportFragmentManager,"Notes Fragment")

            }
            }
        }
    }

    private fun showProgress(){
        binding.progressLoading.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        binding.progressLoading.visibility = View.GONE
    }



}