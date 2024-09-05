package com.stopstone.whathelook.view.mypage

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.whathelook.R
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.databinding.FragmentMyCommentBinding
import com.stopstone.whathelook.utils.KakaoUserUtil
import com.stopstone.whathelook.view.detail.PostDetailActivity
import com.stopstone.whathelook.view.mypage.adapter.MyPostAdapter
import com.stopstone.whathelook.view.mypage.adapter.OnItemClickListener
import com.stopstone.whathelook.view.mypage.viewmodel.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyCommentFragment : Fragment(), OnItemClickListener {
    private var _binding: FragmentMyCommentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyPageViewModel by viewModels()
    private val adapter: MyPostAdapter by lazy { MyPostAdapter(this) }
    private var currentUserId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadCommentList()
        setupRecyclerView()
        collectViewModel()
        fetchCurrentUserId()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collectViewModel() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { collectPostList() }
        }
    }

    private suspend fun collectPostList() {
        viewModel.posts.collect { postList ->
            Log.d("MyPostFragment", "데이터 불러옴")
            adapter.submitList(postList)
        }
    }

    private fun setupRecyclerView() {
        binding.rvMyCommentList.adapter = adapter
        binding.rvMyCommentList.itemAnimator = null
        binding.rvMyCommentList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && !viewModel.isLoading.value
                ) {
                    Log.d("MyPostFragment", "더 많은 데이터 로드")
                    viewModel.loadMoreComments()
                }
            }
        })
    }

    override fun onItemClick(postListItem: PostListItem) {
        val intent = Intent(context, PostDetailActivity::class.java)
        intent.putExtra("post", postListItem)
        startActivity(intent)
    }

    override fun onLikeClick(postListItem: PostListItem) {
        viewModel.updateLikeState(postListItem)
    }


    override fun onMenuClick(postListItem: PostListItem, view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.inflate(R.menu.item_post_menu)

        Log.d("QuestionFragment", "현재 사용자 ID: $currentUserId")
        Log.d("QuestionFragment", "게시물 작성자 ID: ${postListItem.author.kakaoId.toLong()}")

        // 현재 사용자가 게시물 작성자인 경우에만 삭제 메뉴 표시
        if (currentUserId != postListItem.author.kakaoId.toLong()) {
            popup.menu.removeItem(R.id.action_delete)
        } else {
            val deleteItem = popup.menu.findItem(R.id.action_delete)
            deleteItem?.let {
                val spanString = SpannableString(deleteItem.title.toString())
                spanString.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_700
                        )
                    ), 0, spanString.length, 0
                )
                deleteItem.title = spanString
            }
        }

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    viewModel.deletePost(postListItem)
                    true
                }

                else -> false
            }
        }

        popup.show()
    }

    private fun fetchCurrentUserId() = lifecycleScope.launch {
        try {
            currentUserId = KakaoUserUtil.getUserId()
        } catch (e: Exception) {
            Log.e("QuestionFragment", "Failed to fetch current user ID", e)
        }
    }

}