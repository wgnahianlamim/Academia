package com.agon.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.DataStoreManager
import com.agon.app.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = DataStoreManager(application)
    
    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.userProfileFlow.collect { data ->
                _profile.value = data
            }
        }
    }

    fun updateProfile(newProfile: UserProfile) {
        _profile.value = newProfile
        viewModelScope.launch {
            dataStore.saveUserProfile(newProfile)
        }
    }
}
