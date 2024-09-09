package com.stopstone.whathelook.view.custom.gallery.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stopstone.whathelook.databinding.ItemGalleryImageBinding

class CustomGalleryAdapter : ListAdapter<Uri, CustomGalleryAdapter.ImageViewHolder>(ImageDiffCallback()) {

    private var selectedImages: Set<Uri> = emptySet()
    private var onImageClickListener: ((Uri) -> Unit)? = null

    fun setOnImageClickListener(listener: (Uri) -> Unit) {
        onImageClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemGalleryImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ImageViewHolder(private val binding: ItemGalleryImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) {
            Glide.with(binding.root)
                .load(uri)
                .centerCrop()
                .into(binding.ivImage)

            val isSelected = selectedImages.contains(uri)
            binding.ivCheckmark.isVisible = isSelected
            binding.viewOverlay.isVisible = isSelected

            binding.root.setOnClickListener {
                onImageClickListener?.invoke(uri)
            }
        }
    }

    fun updateSelectedImages(newSelectedImages: Set<Uri>) {
        val oldSelectedImages = selectedImages
        selectedImages = newSelectedImages
        notifyDataSetChanged() // 모든 아이템을 다시 그립니다.
    }

    class ImageDiffCallback : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Uri, newItem: Uri) = oldItem == newItem
    }
}