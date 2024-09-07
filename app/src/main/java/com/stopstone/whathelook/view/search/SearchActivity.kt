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

        // 해시태그가 전달되었을 경우 검색 수행 및 검색 결과 프래그먼트로 전환
        intent.getStringExtra("hashtag")?.let { hashtag ->
            setSearchQuery(hashtag)
            performSearch() // 검색 수행
            showSearchResultFragment() // 검색 결과 프래그먼트로 전환
        }
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
    }

    private fun performSearch() {
        val query = binding.etSearch.text.toString().trim()
        if (query.isNotEmpty()) {
            viewModel.searchPosts(query)
            showSearchResultFragment()
            hideKeyboard()
        }
    }

    private fun setSearchQuery(query: String) {
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
            replace(binding.fragmentContainer.id, ResultSearchFragment.newInstance())
            addToBackStack(null)
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

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(binding.fragmentContainer.id)
        if (fragment is RecentSearchFragment) {
            // 검색 결과 프래그먼트가 표시되고 있을 때는 액티비티를 종료
            finish()
        } else {
            // 그 외의 경우 기본 동작 수행
            super.onBackPressed()
        }
    }
}
