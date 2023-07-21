package com.example.notesapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.databinding.NoteLayoutBinding
import com.example.notesapp.model.Note
import java.text.SimpleDateFormat

class NotesAdapter(private val list: List<Note>) : RecyclerView.Adapter<NotesAdapter.UsersViewHolder>() {

    inner class UsersViewHolder(binding: NoteLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        private val binding: NoteLayoutBinding? = DataBindingUtil.bind(itemView)

        @SuppressLint("SimpleDateFormat")
        fun onBind(notes: Note){
            val sdf = SimpleDateFormat("dd MMM yyyy")
            binding!!.note = notes
            binding.date = sdf.format(notes.date).toString()
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val binding: NoteLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.note_layout, parent, false)
        return UsersViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val users = list[position]
        holder.onBind(users)
    }

}