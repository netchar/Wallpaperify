package com.netchar.wallpaperify.ui.about

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.netchar.common.services.IExternalLibraryProvider.Library
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.row_library.view.*

class LicenceAdapter(val onClick: (library: Library) -> Unit) : androidx.recyclerview.widget.ListAdapter<Library, LicenceAdapter.LibraryViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        return LibraryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_library, parent, false))
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class LibraryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Library) {
            itemView.row_library_title.text = item.name
            itemView.row_library_description.text = item.description
            itemView.setOnClickListener { onClick.invoke(item) }
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Library>() {
            override fun areItemsTheSame(oldItem: Library, newItem: Library): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Library, newItem: Library): Boolean {
                return oldItem.description == newItem.description
            }
        }
    }
}