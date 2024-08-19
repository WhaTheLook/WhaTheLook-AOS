package com.stopstone.whathelook.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.stopstone.whathelook.R
import com.stopstone.whathelook.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val questionFragment = QuestionFragment()
    private val answerFragment = AnswerFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragment(questionFragment)
        setTabLayout()

        binding.btnAddPost.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToPost()
            findNavController().navigate(action)
        }

        binding.btnSearch.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToSearch()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().apply {
            replace(binding.containerMain.id, fragment)
            commit()
        }
    }

    private fun setTabLayout() {
        // 탭을 전환하는 방식을 더 고민해 볼 수 있음
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val fragment = when (it.position) {
                        0 -> questionFragment
                        1 -> answerFragment
                        else -> throw IllegalArgumentException(getString(R.string.invalid_tab_position))
                    }
                    setFragment(fragment)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val tabStrip = binding.tabLayout.getChildAt(0) as ViewGroup
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).setBackgroundResource(0)
        }
    }
}