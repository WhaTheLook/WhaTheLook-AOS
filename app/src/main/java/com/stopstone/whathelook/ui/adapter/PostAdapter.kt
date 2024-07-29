package com.stopstone.whathelook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.stopstone.whathelook.data.model.Post
import com.stopstone.whathelook.databinding.ItemAnswerBinding
import com.stopstone.whathelook.databinding.ItemQuestionBinding

class PostAdapter : RecyclerView.Adapter<ViewHolder>() {
    private val items: MutableList<Post> = mutableListOf()
    var onItemClick: ((Post) -> Unit)? = null

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
        return if (items[position].type) VIEW_TYPE_QUESTION else VIEW_TYPE_ANSWER
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<Post>) {
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
        init {
            binding.root.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }

        fun bind(post: Post) {
            binding.tvUserName.text = post.writer.name
            Glide.with(binding.root)
                .load(post.writer.profileImage)
                .circleCrop()
                .into(binding.ivUserProfile)
            binding.tvPostContent.text = post.content
            binding.tvPostTimestamp.text = getRelativeTimeSpan(post.createdAt)
        }

        private fun getRelativeTimeSpan(createdAt: String): String {
            // 여기에 createdAt 문자열을 상대적 시간으로 변환하는 로직을 구현합니다.
            // 예: "2h", "3d" 등
            return "2h" // 임시 반환값
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
        fun bind(post: Post) {
        }
    }

    companion object {
        const val VIEW_TYPE_QUESTION = 0
        const val VIEW_TYPE_ANSWER = 1
    }
}

class PostDiffCallback(
    private val oldList: List<Post>,
    private val newList: List<Post>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}