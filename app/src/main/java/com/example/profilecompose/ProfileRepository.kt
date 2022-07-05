package com.example.profilecompose

import android.util.Log
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException

class ProfileRepository(private val profileStore: DataStore<Profile>) {

    val profileFlow: Flow<Profile> = profileStore.data.catch { exception ->
        if (exception is IOException) {
            Log.e("Profile Compose", "Error reading sort order preferences.", exception)
            emit(Profile.getDefaultInstance())
        } else {
            throw exception
        }
    }

    suspend fun saveDetails(name: String, phone: String, email: String) {
        profileStore.updateData { profile ->
            profile
                .toBuilder()
                .setName(name)
                .setEmail(email)
                .setPhone(phone)
                .build()
        }
    }
}
