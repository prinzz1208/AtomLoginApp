package com.example.atomloginapp.di

import android.content.Context
import com.example.atomloginapp.model.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
class DataStoreModule {
    @Provides
    fun providesPreferenceStorage(@ApplicationContext context: Context):UserPreferences {
        return UserPreferences(context)
    }
}