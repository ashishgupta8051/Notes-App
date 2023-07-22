package com.example.notesapp.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.R
import com.example.notesapp.adapter.ImageListingAdapter
import com.example.notesapp.ui.activity.NotesActivity
import com.example.notesapp.databinding.FragmentNotesBottomSheetBinding
import com.example.notesapp.databinding.FragmentNotesViewBottomSheetBinding
import com.example.notesapp.model.Note
import com.example.notesapp.util.FireStoreCollection
import com.example.notesapp.util.FirebaseStorageConstants
import com.example.notesapp.util.UiState
import com.example.notesapp.util.showMsg
import com.example.notesapp.util.showShortToast
import com.example.notesapp.viewmodel.AuthViewModel
import com.example.notesapp.viewmodel.NoteViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NotesViewBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: FragmentNotesViewBottomSheetBinding
    var note: Note? = null
    private var imageUris:MutableList<Uri> = arrayListOf()
    val adapter by lazy {
        ImageListingAdapter(this,
            onCancelClicked = { pos, item -> onRemoveImage(pos,item)}
        )
    }


    companion object{
        fun getInstance(
           note: Note
        ) = NotesViewBottomSheetFragment().apply{
            arguments = Bundle().apply {
                putParcelable(FireStoreCollection.NOTE, note)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            behavior.isDraggable = true
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesViewBottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        note = arguments?.getParcelable(FireStoreCollection.NOTE)!!

        showNotes()
    }

    private fun showNotes() {
        binding.etTitle.setText(note?.title ?: "")
        binding.etNotes.setText(note?.description ?: "")

        if (note?.text_style == "bold"){
            binding.etNotes.setTypeface(null, Typeface.BOLD)
        }else if (note?.text_style == "italic"){
            binding.etNotes.setTypeface(null, Typeface.ITALIC)
        }else{
            binding.etNotes.setTypeface(null, Typeface.NORMAL)
        }


        binding.images.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        binding.images.adapter = adapter
        binding.images.itemAnimator = null
        imageUris = note?.images?.map { it.toUri() }?.toMutableList() ?: arrayListOf()
        adapter.updateList(imageUris)
    }


    private fun showProgress(){
        binding.progressLoading.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        binding.progressLoading.visibility = View.GONE
    }

    private fun onRemoveImage(pos: Int, item: Uri) {
//        adapter.removeItem(pos)
    }


}
