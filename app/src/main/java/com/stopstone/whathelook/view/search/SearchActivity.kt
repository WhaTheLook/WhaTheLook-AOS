package com.stopstone.whathelook.view.search

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.fragment.app.commit
import com.stopstone.whathelook.data.model.entity.RecentSearch
import com.stopstone.whathelook.databinding.ActivitySearchBinding
import com.stopstone.whathelook.view.search.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private val binding: ActivitySearchBinding by lazy {
        ActivitySearchBinding.inflate(layoutInflater)
    }
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupUI()
        if (savedInstanceState == null) {
            showRecentSearchFragment()
        }
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
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
    }

    fun performSearch() {
        val query = binding.etSearch.text.toString().trim()
        if (query.isNotEmpty()) {
            viewModel.searchPosts(query)
            showSearchResultFragment()
            hideKeyboard()
        }
    }

    fun setSearchQuery(query: String) {
        binding.etSearch.setText(query)
        binding.etSearch.setSelection(query.length)
    }

    private fun showRecentSearchFragment() {
        supportFragmentManager.commit {
            replace(binding.fragmentContainer.id, RecentSearchFragment.newInstance())
        }
    }

    private fun showSearchResultFragment() {
        supportFragmentManager.commit {
            replace(binding.fragmentContainer.id, SearchResultFragment.newInstance())
            addToBackStack(null)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService<InputMethodManager>()
        imm?.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }

    fun onRecentSearchClick(recentSearch: RecentSearch) {
        setSearchQuery(recentSearch.query)
        performSearch()
    }
}