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
import com.stopstone.whathelook.databinding.FragmentMyPostBinding
import com.stopstone.whathelook.view.detail.PostDetailActivity
import com.stopstone.whathelook.view.mypage.adapter.MyPostAdapter
import com.stopstone.whathelook.view.mypage.adapter.OnItemClickListener
import com.stopstone.whathelook.view.mypage.viewmodel.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyPostFragment : Fragment(), OnItemClickListener {
    private var _binding: FragmentMyPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyPageViewModel by viewModels()
    private val adapter: MyPostAdapter by lazy { MyPostAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadPostList()
        setupRecyclerView()
        collectViewModel()
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
        binding.rvMyPostList.adapter = adapter
        binding.rvMyPostList.itemAnimator = null
        binding.rvMyPostList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    viewModel.loadMorePosts()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}