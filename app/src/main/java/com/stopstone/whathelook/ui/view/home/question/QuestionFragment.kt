package com.stopstone.whathelook.ui.view.home.question

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.stopstone.whathelook.data.model.PostListItem
import com.stopstone.whathelook.databinding.FragmentQuestionBinding
import com.stopstone.whathelook.ui.adapter.OnItemClickListener
import com.stopstone.whathelook.ui.adapter.PostAdapter
import com.stopstone.whathelook.ui.view.home.detail.PostDetailActivity
import com.stopstone.whathelook.ui.viewmodel.HomeViewModel
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
        viewModel.loadPostList("질문하기")
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectPostList() }
            }
        }
        binding.rvQuestionList.adapter = adapter
    }

    private suspend fun collectPostList() {
        viewModel.posts.collect { postList ->
            adapter.submitList(postList)
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
}