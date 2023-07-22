package com.example.notesapp.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.res.Resources
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.R
import com.example.notesapp.adapter.ImageListingAdapter
import com.example.notesapp.ui.activity.NotesActivity
import com.example.notesapp.databinding.FragmentNotesBottomSheetBinding
import com.example.notesapp.model.Note
import com.example.notesapp.util.UiState
import com.example.notesapp.util.showMsg
import com.example.notesapp.viewmodel.AuthViewModel
import com.example.notesapp.viewmodel.NoteViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NotesBottomSheetFragment : BottomSheetDialogFragment(),OnClickListener {
    lateinit var binding: FragmentNotesBottomSheetBinding
    private val viewModel: NoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var note: Note? = null
    private var imageUris: MutableList<Uri> = arrayListOf()
    private var notesActivity: NotesActivity? = null
    private var textStyle:String = ""
    private val adapter by lazy {
        ImageListingAdapter(this,
            onCancelClicked = { pos, item -> onRemoveImage(pos,item)}
        )
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
        binding = FragmentNotesBottomSheetBinding.inflate(layoutInflater)

        binding.addImageLl.setOnClickListener(this)
        binding.done.setOnClickListener(this)
        binding.bold.isSelected = false
        binding.italic.isSelected = false
        binding.normal.isSelected = true

        binding.bold.setOnClickListener(this)
        binding.italic.setOnClickListener(this)
        binding.normal.setOnClickListener(this)

        binding.images.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        binding.images.adapter = adapter
        binding.images.itemAnimator = null
        imageUris = note?.images?.map { it.toUri() }?.toMutableList() ?: arrayListOf()
        adapter.updateList(imageUris)

        return binding.root
    }


    fun getActivity(activity: NotesActivity){
        notesActivity = activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    state.error?.let {
                        showMsg(it,binding.tvError,lifecycleScope)
                    }
                }
                is UiState.Success -> {
                    hideProgress()
                    showMsg(state.data.second,binding.tvError,lifecycleScope)
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
            showMsg("Enter notes title",binding.tvError,lifecycleScope)
        } else if (binding.etNotes.text.toString().isNullOrEmpty()) {
            isValid = false
            showMsg("Enter notes description",binding.tvError,lifecycleScope)
        }
        return isValid
    }

    @JvmName("functionOfKotlin")
    private fun getNote(): Note {
        return Note(
            id = note?.id ?: "",
            title = binding.etTitle.text.toString(),
            description = binding.etNotes.text.toString(),
            date = Date(),
            images = getImageUrls(),
            text_style = textStyle,
            ).apply { authViewModel.getSession { this.user_id = it?.id ?: "" } }
    }


    private fun getImageUrls(): List<String> {
        if (imageUris.isNotEmpty()){
            return imageUris.map { it.toString() }
        }else{
            return note?.images ?: arrayListOf()
        }
    }

    private fun showProgress(){
        binding.progressLoading.visibility = View.VISIBLE
    }

    private fun hideProgress(){
        binding.progressLoading.visibility = View.GONE
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when(p0.id){
                R.id.add_image_ll ->{
                    showProgress()
                    ImagePicker.with(this)
                        .compress(1024)
                        .galleryOnly()
                        .createIntent { intent ->
                            startForProfileImageResult.launch(intent)
                        }
                }
                R.id.done->{
                    if (validation()) {
                        if (note == null) {
                            if (imageUris.isNotEmpty()){
                                viewModel.onUploadMultipleFile(imageUris){ state ->
                                    when (state) {
                                        is UiState.Loading -> {
                                            showProgress()
                                        }
                                        is UiState.Failure -> {
                                            hideProgress()
                                            state.error?.let { showMsg(it, binding.tvError, lifecycleScope) }
                                        }
                                        is UiState.Success -> {
                                            hideProgress()
                                            if (note == null) {
                                                viewModel.addNote(getNote())
                                            }
                                        }
                                    }
                                }
                            }else{
                                if (note == null) {
                                    viewModel.addNote(getNote())
                                }
                            }
                        }
                    }
                }
                R.id.bold -> {
                    binding.bold.isSelected = true
                    binding.italic.isSelected = false
                    binding.normal.isSelected = false
                    binding.etNotes.setTypeface(null, Typeface.BOLD)
                    textStyle = "bold"
                }
                R.id.italic -> {
                    binding.bold.isSelected = false
                    binding.italic.isSelected = true
                    binding.normal.isSelected = false
                    binding.etNotes.setTypeface(null, Typeface.ITALIC)
                    textStyle = "italic"
                }
                R.id.normal -> {
                    binding.bold.isSelected = false
                    binding.italic.isSelected = false
                    binding.normal.isSelected = true
                    binding.etNotes.setTypeface(null, Typeface.NORMAL)
                    textStyle = "normal"
                }
            }
        }
    }

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data!!
            imageUris.add(fileUri)
            adapter.updateList(imageUris)
            hideProgress()
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            hideProgress()
            showMsg(ImagePicker.getError(data), binding.tvError, lifecycleScope)
        } else {
            hideProgress()
            Log.e(TAG,"Task Cancelled")
        }
    }

    private fun onRemoveImage(pos: Int, item: Uri) {
        adapter.removeItem(pos)
    }


}
