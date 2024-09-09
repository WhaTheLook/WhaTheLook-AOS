package com.stopstone.whathelook.view.post.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.whathelook.data.model.entity.CreatePostModel
import com.stopstone.whathelook.data.model.response.PostListItem
import com.stopstone.whathelook.domain.usecase.detail.GetPostDetailUseCase
import com.stopstone.whathelook.domain.usecase.post.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val getPostDetailUseCase: GetPostDetailUseCase
) : ViewModel() {

    private val _createPostModelState = MutableStateFlow(CreatePostModel("", "", "", "", "", emptyList(), emptyList()))
    val createPostModelState: StateFlow<CreatePostModel> = _createPostModelState.asStateFlow()

    val isSubmitEnabled = createPostModelState.map { post ->
        post.title.isNotBlank() && post.content.isNotBlank() &&
                post.category.isNotBlank() && post.imageUris.isNotEmpty()
    }

    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages.asStateFlow()

    private val _postTitle = MutableStateFlow("")
    val postTitle: StateFlow<String> = _postTitle.asStateFlow()

    private val _rawContent = MutableStateFlow("")
    private val _postContent = MutableStateFlow("")
    val postContent: StateFlow<String> = _postContent.asStateFlow()

    private val _isChipSelected = MutableStateFlow(false)
    val isChipSelected: StateFlow<Boolean> = _isChipSelected.asStateFlow()

    private val _hashtagList = MutableStateFlow<List<String>>(emptyList())
    val hashtagList: StateFlow<List<String>> = _hashtagList.asStateFlow()

    private val _postCreationResult = MutableSharedFlow<Result<String>>()
    val postCreationResult: SharedFlow<Result<String>> = _postCreationResult.asSharedFlow()

    private val _postUpdateResult = MutableSharedFlow<Result<String>>()
    val postUpdateResult: SharedFlow<Result<String>> = _postUpdateResult.asSharedFlow()

    private val _kakaoId = MutableStateFlow("")
    val kakaoId: StateFlow<String> = _kakaoId.asStateFlow()

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _postDetail = MutableStateFlow<PostListItem?>(null)
    val postDetail: StateFlow<PostListItem?> = _postDetail.asStateFlow()

    fun setPostTitle(title: String) {
        _postTitle.value = title
        updatePostState()
    }

    fun setPostContent(content: String) {
        _rawContent.value = content
        extractHashtags()
        updatePostState()
    }

    fun setCategory(category: String) {
        _category.value = category
        _isChipSelected.value = category.isNotEmpty()
        updatePostState()
    }

    fun addImage(uri: Uri) {
        if (_selectedImages.value.size < MAX_IMAGES) {
            _selectedImages.value += uri
            updatePostState()
        }
    }

    fun removeImage(uri: Uri) {
        _selectedImages.value -= uri
        updatePostState()
    }

    fun setChipSelected(selected: Boolean) {
        _isChipSelected.value = selected
    }

    fun setKakaoId(id: String) {
        _kakaoId.value = id
    }

    fun extractHashtags() {
        val hashtagRegex = "#\\S+".toRegex()
        val hashtags = hashtagRegex.findAll(_rawContent.value)
            .map { it.value }
            .toList()

        _hashtagList.value = hashtags

        val contentWithoutHashtags = _rawContent.value.replace(hashtagRegex, "").trim()
        _postContent.value = contentWithoutHashtags

        Log.d("PostViewModel", "추출된 해시태그: $hashtags")
        Log.d("PostViewModel", "해시태그를 제외한 내용: $contentWithoutHashtags")
    }

    private fun updatePostState() {
        _createPostModelState.value = CreatePostModel(
            title = _postTitle.value,
            content = _postContent.value,
            category = _category.value,
            kakaoId = _kakaoId.value,
            hashtags = _hashtagList.value,
            imageUris = _selectedImages.value
        )
        Log.d("PostViewModel", "게시글 모델 업데이트: ${_createPostModelState.value}")
    }

    fun createPost() {
        viewModelScope.launch {
            Log.d("PostViewModel", "게시글 생성 시도: ${_createPostModelState.value}")
            try {
                val result = createPostUseCase(_createPostModelState.value)
                _postCreationResult.emit(result)
                Log.d("PostViewModel", "게시글 생성 성공: $result")
            } catch (e: Exception) {
                _postCreationResult.emit(Result.failure(e))
                Log.e("PostViewModel", "게시글 생성 실패: ${e.message}")
            }
        }
    }

    fun updatePost(postId: Long) {
        viewModelScope.launch {
            Log.d("PostViewModel", "게시글 수정 시도: ${_createPostModelState.value}")
            try {
//                val result = updatePostUseCase(postId, _createPostModelState.value)
//                _postUpdateResult.emit(result)
                Log.d("PostViewModel", "게시글 수정 성공: ")
            } catch (e: Exception) {
                _postUpdateResult.emit(Result.failure(e))
                Log.e("PostViewModel", "게시글 수정 실패: ${e.message}")
            }
        }
    }

    fun getPostDetail(postId: Long) {
        viewModelScope.launch {
            try {
                val post = getPostDetailUseCase(postId)
                _postDetail.value = post
                Log.d("PostViewModel", "게시글 상세 정보 조회 성공: $post")
            } catch (e: Exception) {
                Log.e("PostViewModel", "게시글 상세 정보 조회 실패: ${e.message}")
            }
        }
    }

    fun setSelectedImages(images: List<Uri>) {
        _selectedImages.value = images
        updatePostState()
    }

    companion object {
        private const val MAX_IMAGES = 5
    }
}