package com.stopstone.whathelook.view.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.databinding.FragmentQuestionBinding
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
        viewModel.loadPostList(CATEGORY)
        setupRecyclerView()
        collectViewModel()
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

    private fun setupRecyclerView() {
        binding.rvQuestionList.adapter = adapter
        binding.rvQuestionList.itemAnimator = null
    }

    private fun collectViewModel() = lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { collectPostList() }
        }
    }

    private suspend fun collectPostList() {
        viewModel.posts.collect { postList ->
            adapter.submitList(postList)
        }
    }

    companion object {
        private const val CATEGORY = "질문하기"
    }
}