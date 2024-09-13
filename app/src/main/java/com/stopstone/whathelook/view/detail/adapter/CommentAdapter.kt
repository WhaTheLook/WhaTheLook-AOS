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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            ItemPostCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    override fun getItemCount(): Int = commentList.size

    fun submitList(comments: List<Comment>) {
        commentList.clear()
        commentList.addAll(comments)
        notifyDataSetChanged()
    }

    class CommentViewHolder(
        private val binding: ItemPostCommentBinding,
        private val listener: OnCommentClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: Comment) {
            binding.apply {
                tvUserName.text = comment.author.name
                ivUserProfile.loadCircleImage(comment.author.profileImage)
                tvPostCommentContent.text = comment.text
                tvPostCommentTimestamp.setRelativeTimeText(comment.date)

                btnPostCommentMenu.setOnClickListener {
                    listener.onMenuClick(comment, it)
                }

                tvPostCommentReply.setOnClickListener {
                    listener.onReplyClick(comment)
                }

                // 대댓글 보기 기능 추가 (옵션)
                if (comment.replyComment?.isNotEmpty() == true) {
                    tvPostCommentReplyVisible.visibility = View.VISIBLE
                    tvPostCommentReplyVisible.setOnClickListener {
                        listener.onShowRepliesClick(comment)
                    }
                } else {
                    tvPostCommentReplyVisible.visibility = View.GONE
                }
            }
        }
    }
}

interface OnCommentClickListener {
    fun onMenuClick(comment: Comment, view: View)
    fun onReplyClick(comment: Comment)
    fun onShowRepliesClick(comment: Comment) // 대댓글 보기 기능 (옵션)
}