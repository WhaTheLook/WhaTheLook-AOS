package com.stopstone.whathelook.view.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.whathelook.data.model.entity.RecentSearch
import com.stopstone.whathelook.databinding.ItemSearchHistoryListBinding

class SearchHistoryAdapter(
    private val onItemClick: (RecentSearch) -> Unit,
    private val onDeleteClick: (RecentSearch) -> Unit
) : ListAdapter<RecentSearch, SearchHistoryAdapter.ViewHolder>(RecentSearchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemSearchHistoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(search: RecentSearch) {
            binding.tvSearchText.text = search.query
            binding.root.setOnClickListener { onItemClick(search) }
            binding.btnSearchCancel.setOnClickListener { onDeleteClick(search) }
        }
    }
}

class RecentSearchDiffCallback : DiffUtil.ItemCallback<RecentSearch>() {
    override fun areItemsTheSame(oldItem: RecentSearch, newItem: RecentSearch): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RecentSearch, newItem: RecentSearch): Boolean {
        return oldItem == newItem
    }
}