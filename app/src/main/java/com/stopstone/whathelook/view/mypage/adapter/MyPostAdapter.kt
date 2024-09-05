package com.stopstone.whathelook.view.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.databinding.ItemQuestionBinding
import com.stopstone.whathelook.utils.HashtagUtils
import com.stopstone.whathelook.utils.loadCircleImage
import com.stopstone.whathelook.utils.setRelativeTimeText
import com.stopstone.whathelook.view.post.adapter.PostDiffCallback
import com.stopstone.whathelook.view.post.adapter.PostListItemImageAdapter


class MyPostAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<MyPostAdapter.MyPostViewHolder>() {
    private val items: MutableList<PostListItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        return MyPostViewHolder(
            parent,
            onClickListener = { position -> listener.onItemClick(items[position]) },
            onLikeClickListener = { position -> listener.onLikeClick(items[position]) },
            onMenuClickListener = { position, view ->
                listener.onMenuClick(
                    items[position],
                    view
                )
            }
        )
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<PostListItem>) {
        val diffCallback = PostDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }


    class MyPostViewHolder(
        parent: ViewGroup,
        private val onClickListener: (position: Int) -> Unit,
        private val onLikeClickListener: (position: Int) -> Unit,
        private val onMenuClickListener: (position: Int, view: android.view.View) -> Unit,
        private val binding: ItemQuestionBinding = ItemQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
    ) : RecyclerView.ViewHolder(binding.root) {
        private val postListItemImageAdapter = PostListItemImageAdapter()

        init {
            binding.root.setOnClickListener {
                onClickListener(adapterPosition)
            }


            binding.btnPostLike.setOnClickListener {
                onLikeClickListener(adapterPosition)
            }

            binding.btnPostMenu.setOnClickListener {
                onMenuClickListener(adapterPosition, it)
            }


            binding.rvPostImageList.apply {
                adapter = postListItemImageAdapter
            }
        }

        fun bind(postListItem: PostListItem) {
            with(binding) {
                with(postListItem) {
                    ivUserProfile.loadCircleImage(author.profileImage)
                    tvUserName.text = author.name
                    tvPostTimestamp.setRelativeTimeText(date)
                    tvPostContent.text = content
                    postListItemImageAdapter.submitList(photoUrls)
                    btnPostLike.isSelected = likeYN
                    tvPostLikeCount.text = "$likeCount"
                    tvPostCommentCount.text = "$commentCount"

                    // 본문에 해시태그 적용
                    HashtagUtils.setClickableHashtags(
                        root.context,
                        tvPostContent,
                        content,
                        hashtags
                    )

                    // 해시태그 목록 표시
                    val hashtagContent = hashtags.joinToString(" ")
                    tvPostHashtags.text = hashtagContent
                    HashtagUtils.setClickableHashtags(
                        root.context,
                        tvPostHashtags,
                        hashtagContent,
                        hashtags
                    )
                }
            }
        }
    }
}

interface OnItemClickListener {
    fun onItemClick(postListItem: PostListItem)
    fun onLikeClick(postListItem: PostListItem)
    fun onMenuClick(postListItem: PostListItem, view: View)
}