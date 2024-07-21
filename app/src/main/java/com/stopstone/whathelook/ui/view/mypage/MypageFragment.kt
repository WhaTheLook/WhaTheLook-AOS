package com.stopstone.whathelook.ui.view.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.stopstone.whathelook.databinding.FragmentMypageBinding
import com.stopstone.whathelook.ui.view.mypage.mycomment.MyCommentFragment
import com.stopstone.whathelook.ui.view.mypage.mypost.MyPostFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MypageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private val adapter: ViewPagerAdapter by lazy { ViewPagerAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPagerMypage.adapter = adapter

        // TabLayout과 ViewPager2 연결
        TabLayoutMediator(binding.tabLayout, binding.viewPagerMypage) { tab, position ->
            tab.text = when (position) {
                0 -> "내 게시물"
                1 -> "내 댓글"
                else -> null
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyPostFragment()
            1 -> MyCommentFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}