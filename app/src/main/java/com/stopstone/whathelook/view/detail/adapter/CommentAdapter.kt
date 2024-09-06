package com.stopstone.whathelook.view.detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.whathelook.data.model.response.Comment
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.databinding.ItemPostCommentBinding
import com.stopstone.whathelook.utils.loadCircleImage
import com.stopstone.whathelook.utils.setRelativeTimeText

class CommentAdapter(private val listener: OnCommentClickListener) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    private val commentList = mutableListOf<Comment>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {
        return CommentViewHolder(
            parent,
            onCommentClickListener = { position, view -> listener.onMenuClick(commentList[position], view) }
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    fun submitList(comments: List<Comment>) {
        commentList.clear()
        commentList.addAll(comments)
        notifyDataSetChanged()
    }

    class CommentViewHolder(
        parent: ViewGroup,
        private val binding: ItemPostCommentBinding = ItemPostCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        private val onCommentClickListener: (position: Int, view: View) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.tvUserName.text = comment.author.name
            binding.ivUserProfile.loadCircleImage(comment.author.profileImage)
            binding.tvPostCommentContent.text = comment.text
            binding.tvPostCommentTimestamp.setRelativeTimeText(comment.date)
        }

        init {
            binding.btnPostCommentMenu.setOnClickListener {
                onCommentClickListener(adapterPosition, it)
            }
        }
    }
}

interface OnCommentClickListener {
    fun onMenuClick(comment: Comment, view: View)
}