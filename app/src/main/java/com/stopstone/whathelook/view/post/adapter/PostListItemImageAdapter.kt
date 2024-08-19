package com.stopstone.whathelook.view.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stopstone.whathelook.databinding.ItemPostListImageBinding

class PostListItemImageAdapter :
    RecyclerView.Adapter<PostListItemImageAdapter.PostListItemImageViewHolder>() {
    private val items: MutableList<String> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListItemImageViewHolder {
        val binding =
            ItemPostListImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostListItemImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostListItemImageViewHolder, position: Int) {
        holder.bind(items[position])
    }


    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(newItems: List<String>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class PostListItemImageViewHolder(private val binding: ItemPostListImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            Glide.with(binding.root)
                .load(item)
                .centerCrop()
                .into(binding.ivPostImage)
        }
    }
}
