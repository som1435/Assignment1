package com.example.profilecompose

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProfileViewModel(profileStore: DataStore<Profile>) : ViewModel() {

    private var profileRepository: ProfileRepository

    init {
        profileRepository = ProfileRepository(profileStore)
    }

    val profileFlow: Flow<Profile> = profileRepository.profileFlow

    suspend fun saveDetails(name: String, phone: String, email: String) {
        viewModelScope.launch {
            profileRepository.saveDetails(name, phone, email)
        }
    }

}

class ProfileViewModelFactory(private val profileStore: DataStore<Profile>) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(profileStore) as T
    }
}