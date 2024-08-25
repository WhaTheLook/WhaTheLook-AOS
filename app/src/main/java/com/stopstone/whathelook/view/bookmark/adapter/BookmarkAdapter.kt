package com.stopstone.whathelook.view.bookmark.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.databinding.ItemAnswerBinding
import com.stopstone.whathelook.utils.loadCenterCropImage

class BookmarkAdapter: ListAdapter<PostListItem, BookmarkAdapter.BookmarkViewHolder>(BookmarkDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        return BookmarkViewHolder(
            ItemAnswerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class BookmarkViewHolder(private val binding: ItemAnswerBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(postListItem: PostListItem) {
            binding.ivPostImage.loadCenterCropImage(postListItem.photoUrls.first())
        }
    }
}

class BookmarkDiffCallback : DiffUtil.ItemCallback<PostListItem>() {
    override fun areItemsTheSame(oldItem: PostListItem, newItem: PostListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PostListItem, newItem: PostListItem): Boolean {
        return oldItem == newItem
    }
}