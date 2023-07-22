package com.example.notesapp.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.ImageListItemBinding
import com.example.notesapp.ui.fragment.NotesViewBottomSheetFragment

class ImageListingAdapter(private val fragment: Fragment,
    val onCancelClicked: ((Int, Uri) -> Unit)? = null,
) : RecyclerView.Adapter<ImageListingAdapter.MyViewHolder>() {

    private var list: MutableList<Uri> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ImageListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item,position)

        if (fragment is NotesViewBottomSheetFragment){
            holder.binding.cancel.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<Uri>){
        this.list = list
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(val binding: ImageListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Uri,position: Int) {
            binding.image.setImageURI(item)
            binding.cancel.setOnClickListener {
                onCancelClicked?.invoke(adapterPosition, item)
            }
        }
    }
}