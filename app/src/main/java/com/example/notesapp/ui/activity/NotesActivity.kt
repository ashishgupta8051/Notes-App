package com.example.notesapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.adapter.NotesAdapter
import com.example.notesapp.databinding.ActivityMainBinding
import com.example.notesapp.model.Note
import com.example.notesapp.ui.fragment.NotesBottomSheetFragment
import com.example.notesapp.ui.fragment.NotesViewBottomSheetFragment
import com.example.notesapp.util.NotesClickListener
import com.example.notesapp.util.UiState
import com.example.notesapp.util.showShortToast
import com.example.notesapp.viewmodel.AuthViewModel
import com.example.notesapp.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


@AndroidEntryPoint
class NotesActivity : AppCompatActivity(), OnClickListener, NotesClickListener {
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


        notesAdapter = NotesAdapter(noteList, this)
        binding.noteListing.layoutManager = LinearLayoutManager(this)
        binding.noteListing.adapter = notesAdapter

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.noteListing)

        observer()
        getNotes()
    }

    private fun getNotes() {
        authViewModel.getSession {
            notesViewModel.getNotes(it)
        }
    }


    val simpleCallback: ItemTouchHelper.SimpleCallback = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            when (direction) {
                ItemTouchHelper.LEFT -> {
                    val note = noteList[position]
                    notesViewModel.deleteNote(note)
                }
            }
        }

        override fun onChildDraw(c: Canvas,recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            RecyclerViewSwipeDecorator.Builder(this@NotesActivity, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@NotesActivity, R.color.colorAccent))
                .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                .setActionIconTint(ContextCompat.getColor(recyclerView.context, android.R.color.white))
                .create()
                .decorate()
        }

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
                    noteList.clear()
                    noteList.addAll(state.data.toMutableList())
                    notesAdapter.notifyDataSetChanged()
                }

                else -> {}
            }
        }


        notesViewModel.deleteNote.observe(this){
            state->
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
                    showShortToast(this, "Note deleted successfully.")
                    getNotes()
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

    override fun onClickListener(note: Note) {
        val bottomSheet = NotesViewBottomSheetFragment.getInstance(note)
        bottomSheet.show(supportFragmentManager,"Note View")
    }


}