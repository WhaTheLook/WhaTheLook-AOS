package com.stopstone.whathelook.view.home

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
import com.stopstone.whathelook.databinding.FragmentQuestionBinding
import com.stopstone.whathelook.utils.KakaoUserUtil
import com.stopstone.whathelook.view.detail.PostDetailActivity
import com.stopstone.whathelook.view.home.viewmodel.HomeViewModel
import com.stopstone.whathelook.view.post.adapter.OnItemClickListener
import com.stopstone.whathelook.view.post.adapter.PostAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuestionFragment : Fragment(), OnItemClickListener {
    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!
    private val adapter: PostAdapter by lazy { PostAdapter(this) }
    private val viewModel: HomeViewModel by viewModels()
    private var currentUserId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSwipeRefresh()
        setupRecyclerView()
        collectViewModel()
        fetchCurrentUserId()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(postListItem: PostListItem) {
        val intent = Intent(context, PostDetailActivity::class.java)
        intent.putExtra("post", postListItem)
        startActivity(intent)
    }

    override fun onLikeClick(postListItem: PostListItem) {
        viewModel.updateLikeState(postListItem)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        viewModel.loadPostList(CATEGORY)
    }

    private fun setupRecyclerView() {
        binding.rvQuestionList.adapter = adapter
        binding.rvQuestionList.itemAnimator = null
        binding.rvQuestionList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    Log.d("QuestionFragment", "더 많은 데이터 로드")
                    viewModel.loadMorePosts(CATEGORY)
                }
            }
        })
    }

    private fun collectViewModel() = lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { collectPostList() }
            launch { collectLoadingState() }
        }
    }

    private suspend fun collectPostList() {
        viewModel.posts.collect { postList ->
            Log.d("QuestionFragment", "데이터 불러옴")
            adapter.submitList(postList)
        }
    }

    private suspend fun collectLoadingState() {
        viewModel.isLoading.collect { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }
    }

    private fun fetchCurrentUserId() = lifecycleScope.launch {
        try {
            currentUserId = KakaoUserUtil.getUserId()
        } catch (e: Exception) {
            Log.e("QuestionFragment", "Failed to fetch current user ID", e)
        }
    }

    companion object {
        private const val CATEGORY = "질문하기"
    }
}