package com.stopstone.whathelook.view.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.stopstone.whathelook.data.model.response.UserInfo
import com.stopstone.whathelook.databinding.FragmentMypageBinding
import com.stopstone.whathelook.view.mypage.viewmodel.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MypageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private val adapter: ViewPagerAdapter by lazy { ViewPagerAdapter(this) }
    private val viewModel: MyPageViewModel by viewModels()

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
        viewModel.fetchUserInfo()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectUserInfo() }
            }
        }

        // TabLayout과 ViewPager2 연결
        TabLayoutMediator(binding.tabLayout, binding.viewPagerMypage) { tab, position ->
            tab.text = when (position) {
                0 -> "내 게시물"
                1 -> "내 댓글"
                else -> null
            }
        }.attach()

        binding.btnMypageEditProfile.setOnClickListener {
            lifecycleScope.launch {
                viewModel.uiState.collect { userInfo ->
                    if (userInfo != null) {
                        val action =
                            MypageFragmentDirections.actionMypageToEditProfileActivity(userInfo)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private suspend fun collectUserInfo() {
        viewModel.uiState.collect { userInfo ->
            userInfo?.let { updateUI(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(userInfo: UserInfo) {
        binding.tvMypageUserName.text = userInfo.name
        Glide.with(this)
            .load(userInfo.profileImage)
            .into(binding.ivMypageProfileImage)
        binding.tvMypageUserPost.text = userInfo.postCount.toString()
        binding.tvMypageUserComment.text = userInfo.commentCount.toString()
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
