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
import com.stopstone.whathelook.view.post.adapter.PostListItemImageAdapter

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

        private val childCommentAdapter = CommentAdapter(listener)

        init {
            binding.rvChildComments.apply {
                adapter = childCommentAdapter
                visibility = View.GONE
            }
        }

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

                if (comment.children.isNotEmpty()) {
                    tvPostCommentReplyVisible.visibility = View.VISIBLE
                    tvPostCommentReplyVisible.text = "${comment.children.size}개의 답글 보기"
                    tvPostCommentReplyVisible.setOnClickListener {
                        listener.onShowRepliesClick(comment)
                        toggleChildComments(comment)
                    }
                } else {
                    tvPostCommentReplyVisible.visibility = View.GONE
                }
            }
        }

        private fun toggleChildComments(comment: Comment) {
            binding.apply {
                if (rvChildComments.visibility == View.VISIBLE) {
                    rvChildComments.visibility = View.GONE
                    tvPostCommentReplyVisible.text = "${comment.children.size}개의 답글 보기"
                } else {
                    rvChildComments.visibility = View.VISIBLE
                    childCommentAdapter.submitList(comment.children)
                    tvPostCommentReplyVisible.text = "답글 숨기기"
                }
            }
        }
    }
}

interface OnCommentClickListener {
    fun onMenuClick(comment: Comment, view: View)
    fun onReplyClick(comment: Comment)
    fun onShowRepliesClick(comment: Comment)
}