package com.stopstone.whathelook.view.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.whathelook.data.model.response.Comment
import com.stopstone.whathelook.databinding.ItemPostCommentBinding
import com.stopstone.whathelook.utils.loadCircleImage
import com.stopstone.whathelook.utils.setRelativeTimeText

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
    private val commentList = mutableListOf<Comment>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {
        return CommentViewHolder(
            ItemPostCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
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

    class CommentViewHolder(private val binding: ItemPostCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.tvUserName.text = comment.author.name
            binding.ivUserProfile.loadCircleImage(comment.author.profileImage)
            binding.tvPostCommentContent.text = comment.text
            binding.tvPostCommentTimestamp.setRelativeTimeText(comment.date)
        }
    }
}