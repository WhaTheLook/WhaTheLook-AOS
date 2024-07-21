package com.stopstone.whathelook.ui.view.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.stopstone.whathelook.data.model.User
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

        binding.btnMypageEditProfile.setOnClickListener {
            // 프로필 수정 버튼 클릭 시 처리
            val action = MypageFragmentDirections.actionMypageToEditProfileActivity(
                User(
                    2,
                    "Annonymous",
                    "https://thumbnail8.coupangcdn.com/thumbnails/remote/492x492ex/image/rs_quotation_api/sfljdb3g/a0514217b99140b69bde6cb66d2ee914.jpg",
                    "2023-02-15"
                ),
            )
            findNavController().navigate(action)
        }
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