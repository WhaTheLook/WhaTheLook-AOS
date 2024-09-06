package com.stopstone.whathelook.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.stopstone.whathelook.databinding.FragmentRecentSearchBinding
import com.stopstone.whathelook.view.search.adapter.SearchHistoryAdapter
import com.stopstone.whathelook.view.search.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

class RecentSearchFragment : Fragment() {
    private var _binding: FragmentRecentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()

        binding.tvClearAll.setOnClickListener {
            viewModel.clearAllSearches()
        }
    }

    private fun setupRecyclerView() {
        searchHistoryAdapter = SearchHistoryAdapter(
            onItemClick = { search ->
                (activity as? SearchActivity)?.onRecentSearchClick(search)
            },
            onDeleteClick = { search ->
                viewModel.deleteSearch(search)
            }
        )
        binding.rvSearchHistoryList.apply {
            adapter = searchHistoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = RecentSearchFragment()
    }
}
