package com.stopstone.whathelook.view.search

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.stopstone.whathelook.R
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.databinding.FragmentSearchResultBinding
import com.stopstone.whathelook.utils.KakaoUserUtil
import com.stopstone.whathelook.view.detail.PostDetailActivity
import com.stopstone.whathelook.view.search.adapter.OnItemClickListener
import com.stopstone.whathelook.view.search.adapter.SearchResultAdapter
import com.stopstone.whathelook.view.search.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import androidx.activity.OnBackPressedCallback

class ResultSearchFragment : Fragment(), OnItemClickListener {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var searchResultAdapter: SearchResultAdapter
    private var currentUserId: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        fetchCurrentUserId()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 검색 결과 프래그먼트에서 뒤로가기를 누를 때 액티비티 종료
                requireActivity().finish()
            }
        })
    }

    private fun setupRecyclerView() {
        searchResultAdapter = SearchResultAdapter(this)
        binding.rvSearchResults.apply {
            adapter = searchResultAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResults.collect { results ->
                if (results.isNotEmpty()) {
                    binding.rvSearchResults.visibility = View.VISIBLE
                    binding.tvNoResults.visibility = View.GONE
                    searchResultAdapter.submitList(results)
                } else {
                    binding.rvSearchResults.visibility = View.GONE
                    binding.tvNoResults.visibility = View.VISIBLE
                }
            }
        }
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

    override fun onMenuClick(postListItem: PostListItem, view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.inflate(R.menu.item_post_menu)

        Log.d("SearchResultFragment", "현재 사용자 ID: $currentUserId")
        Log.d("SearchResultFragment", "게시물 작성자 ID: ${postListItem.author.kakaoId.toLong()}")

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
            Log.e("SearchResultFragment", "Failed to fetch current user ID", e)
        }
    }

    companion object {
        fun newInstance() = ResultSearchFragment()
    }
}
