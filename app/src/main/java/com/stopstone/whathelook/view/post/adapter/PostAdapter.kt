package com.stopstone.whathelook.view.post.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.databinding.ItemAnswerBinding
import com.stopstone.whathelook.databinding.ItemQuestionBinding
import com.stopstone.whathelook.utils.HashtagUtils
import com.stopstone.whathelook.utils.loadCenterCropImage
import com.stopstone.whathelook.utils.loadCircleImage
import com.stopstone.whathelook.utils.setRelativeTimeText

class PostAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<ViewHolder>() {
    private val items: MutableList<PostListItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_QUESTION -> QuestionViewHolder(
                parent,
                onClickListener = { position -> listener.onItemClick(items[position]) },
                onLikeClickListener = { position -> listener.onLikeClick(items[position]) },
            )

            VIEW_TYPE_ANSWER -> AnswerViewHolder(parent,
                onClickListener = { position -> listener.onItemClick(items[position]) })

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
        private val onClickListener: (position: Int) -> Unit,
        private val onLikeClickListener: (position: Int) -> Unit,
        private val binding: ItemQuestionBinding = ItemQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
    ) : ViewHolder(binding.root) {
        private val postListItemImageAdapter = PostListItemImageAdapter()

        init {
            binding.root.setOnClickListener {
                onClickListener(adapterPosition)
            }


            binding.btnPostLike.setOnClickListener {
                onLikeClickListener(adapterPosition)
            }

            binding.rvPostImageList.apply {
                adapter = postListItemImageAdapter
            }
        }

        fun bind(postListItem: PostListItem) {
            with(binding) {
                binding.btnPostMenu.visibility = GONE
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

    class AnswerViewHolder(
        parent: ViewGroup,
        private val binding: ItemAnswerBinding = ItemAnswerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        private val onClickListener: (position: Int) -> Unit,
    ) : ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onClickListener(adapterPosition)
            }
        }

        fun bind(postListItem: PostListItem) {
            binding.ivPostImage.loadCenterCropImage(postListItem.photoUrls.first())
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

interface OnItemClickListener {
    fun onItemClick(postListItem: PostListItem)
    fun onLikeClick(postListItem: PostListItem)
}