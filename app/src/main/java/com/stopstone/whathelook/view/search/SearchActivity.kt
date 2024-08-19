package com.stopstone.whathelook.view.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.stopstone.whathelook.databinding.ActivitySearchBinding
import com.stopstone.whathelook.view.search.adapter.SearchHistoryAdapter
import com.stopstone.whathelook.view.search.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(
            layoutInflater
        )
    }
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
        focusSearchEditText()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }


        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        setupRecyclerView()

        binding.tvClearAll.setOnClickListener {
            viewModel.clearAllSearches()
        }
    }

    private fun setupRecyclerView() {
        searchHistoryAdapter = SearchHistoryAdapter(
            onItemClick = { search ->
                binding.etSearch.setText(search.query)
                binding.etSearch.setSelection(search.query.length)
            },
            onDeleteClick = { search ->
                viewModel.deleteSearch(search)
            }
        )
        binding.rvSearchHistoryList.apply {
            adapter = searchHistoryAdapter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.recentSearches.collect { searches ->
                if (searches.isEmpty()) {
                    binding.groupRecentSearchesEmpty.visibility = View.VISIBLE
                    binding.groupRecentSearches.visibility = View.GONE
                } else {
                    binding.groupRecentSearches.visibility = View.VISIBLE
                    binding.groupRecentSearchesEmpty.visibility = View.GONE
                    searchHistoryAdapter.submitList(searches)
                }
            }
        }
    }

    private fun focusSearchEditText() = lifecycleScope.launch {
        delay(300) // 약간의 지연 추가
        binding.etSearch.requestFocus()
        showKeyboard(binding.etSearch)
    }

    private fun performSearch() {
        val query = binding.etSearch.text.toString().trim()
        if (query.isNotEmpty()) {
            viewModel.addSearch(query)
            binding.etSearch.text.clear()
        }
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService<InputMethodManager>()
        imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}