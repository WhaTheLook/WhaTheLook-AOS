package com.stopstone.whathelook.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.stopstone.whathelook.data.model.response.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserManager(private val dataStore: DataStore<Preferences>) {

    // 저장할 유저 정보 정의
    private object PreferencesKeys {
        val KAKAO_ID = stringPreferencesKey("kakao_id")
        val EMAIL = stringPreferencesKey("email")
        val NAME = stringPreferencesKey("name")
        val PROFILE_IMAGE = stringPreferencesKey("profile_image")
        val DATE = stringPreferencesKey("date")
    }

    val userInfoFlow: Flow<UserInfo?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            mapUserInfo(preferences)
        }

    suspend fun saveUserInfo(userInfo: UserInfo) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.KAKAO_ID] = userInfo.kakaoId
            preferences[PreferencesKeys.EMAIL] = userInfo.email
            preferences[PreferencesKeys.NAME] = userInfo.name
            preferences[PreferencesKeys.PROFILE_IMAGE] = userInfo.profileImage
            preferences[PreferencesKeys.DATE] = userInfo.date
        }
    }

    suspend fun clearUserInfo() {
        dataStore.edit { it.clear() }
    }

    suspend fun getUserInfo(): UserInfo? {
        val preferences = dataStore.data.first()
        return mapUserInfo(preferences)
    }

    // UserInfo 매핑 함수
    private fun mapUserInfo(preferences: Preferences): UserInfo? {
        val kakaoId = preferences[PreferencesKeys.KAKAO_ID] ?: return null
        val email = preferences[PreferencesKeys.EMAIL] ?: return null
        val name = preferences[PreferencesKeys.NAME] ?: return null
        val profileImage = preferences[PreferencesKeys.PROFILE_IMAGE] ?: return null
        val date = preferences[PreferencesKeys.DATE] ?: return null

        return UserInfo(kakaoId, email, name, profileImage, date)
    }
}