package com.stopstone.whathelook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.stopstone.whathelook.data.model.CreatePostModel
import com.stopstone.whathelook.data.model.PostListItem
import com.stopstone.whathelook.databinding.ItemAnswerBinding
import com.stopstone.whathelook.databinding.ItemQuestionBinding

class PostAdapter : RecyclerView.Adapter<ViewHolder>() {
    private val items: MutableList<PostListItem> = mutableListOf()
    var onItemClick: ((PostListItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_QUESTION -> QuestionViewHolder(
                parent,
                { position -> onItemClick?.invoke(items[position]) })

            VIEW_TYPE_ANSWER -> AnswerViewHolder(parent)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is QuestionViewHolder -> holder.bind(item)
            is AnswerViewHolder -> holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].category == "질문하기") VIEW_TYPE_QUESTION else VIEW_TYPE_ANSWER
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<PostListItem>) {
        val diffCallback = PostDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    class QuestionViewHolder(
        parent: ViewGroup,
        private val onItemClick: (Int) -> Unit,
        private val binding: ItemQuestionBinding = ItemQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
    ) : ViewHolder(binding.root) {
        private val postListItemImageAdapter = PostListItemImageAdapter()
        init {
            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }

            binding.rvPostImageList.apply {
                adapter = postListItemImageAdapter
            }
        }

        fun bind(postListItem: PostListItem) {
            Glide.with(binding.root)
                .load(postListItem.author.profileImage)
                .circleCrop()
                .into(binding.ivUserProfile)
            binding.tvUserName.text = postListItem.author.name
            binding.tvPostTimestamp.text = postListItem.date
            binding.tvPostContent.text = postListItem.content
            postListItemImageAdapter.submitList(postListItem.photoUrls)
        }
    }

    class AnswerViewHolder(
        parent: ViewGroup,
        private val binding: ItemAnswerBinding = ItemAnswerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
    ) : ViewHolder(binding.root) {
        fun bind(createPostModel: PostListItem) {
        }
    }

    companion object {
        const val VIEW_TYPE_QUESTION = 0
        const val VIEW_TYPE_ANSWER = 1
    }
}

class PostDiffCallback(
    private val oldList: List<PostListItem>,
    private val newList: List<PostListItem>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}