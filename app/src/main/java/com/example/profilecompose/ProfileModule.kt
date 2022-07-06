package com.example.profilecompose

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    private val Context.profileStore: DataStore<Profile> by dataStore(
        fileName = "profile.pb",
        serializer = ProfileSerializer
    )

    @Provides
    @Reusable
    fun provideProtoDataStore(@ApplicationContext context: Context): DataStore<Profile> =
        context.profileStore

    @Singleton
    @Provides
    fun provideRepository(profileStore: DataStore<Profile>): ProfileRepository =
        ProfileRepository(profileStore)
}