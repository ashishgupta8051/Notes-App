package com.example.notesapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.notesapp.ui.activity.NotesActivity
import com.example.notesapp.databinding.FragmentNotesBottomSheetBinding
import com.example.notesapp.model.Note
import com.example.notesapp.util.UiState
import com.example.notesapp.util.showShortToast
import com.example.notesapp.viewmodel.AuthViewModel
import com.example.notesapp.viewmodel.NoteViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NotesBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentNotesBottomSheetBinding
    val viewModel: NoteViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()
    var note: Note? = null
    private var notesActivity: NotesActivity? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }


    fun getActivity(activity: NotesActivity){
        notesActivity = activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.done.setOnClickListener {
            if (validation()) {
                if (note == null) {
                    viewModel.addNote(getNote())
                }
            }
        }
        observer()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observer(){
        viewModel.addNote.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    showProgress()
                }
                is UiState.Failure -> {
                    hideProgress()
                    state.error?.let { showShortToast(requireContext(), it) }
                }
                is UiState.Success -> {
                    hideProgress()
                    showShortToast(requireContext(),state.data.second)
                    this.dismiss()
                    notesActivity?.noteList?.add(state.data.first)
                    notesActivity?.notesAdapter?.notifyDataSetChanged()

                }
            }
        }
    }

    private fun validation(): Boolean {
        var isValid = true
        if (binding.etTitle.text.toString().isNullOrEmpty()) {
            isValid = false
            showShortToast(requireContext(),"Enter notes title")
        } else if (binding.etNotes.text.toString().isNullOrEmpty()) {
            isValid = false
            showShortToast(requireContext(),"Enter notes description")
        }
        return isValid
    }

    @JvmName("functionOfKotlin")
    private fun getNote(): Note {
        return Note(
            id = note?.id ?: "",
            title = binding.etTitle.text.toString(),
            description = binding.etNotes.text.toString(),
            date = Date()
        ).apply { authViewModel.getSession { this.user_id = it?.id ?: "" } }
    }

    private fun showProgress(){
        binding.progressLoading.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        binding.progressLoading.visibility = View.GONE
    }


}
