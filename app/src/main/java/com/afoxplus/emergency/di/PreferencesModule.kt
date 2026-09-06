package com.afoxplus.emergency.di

import android.content.Context
import com.afoxplus.emergency.presentation.contacts.ContactsRepository
import com.afoxplus.emergency.presentation.contacts.ContactsRepositoryImpl
import com.afoxplus.emergency.presentation.contacts.EmergencyContactRepository
import com.afoxplus.emergency.presentation.contacts.EmergencyContactRepositoryImpl
import com.afoxplus.emergency.presentation.onboarding.OnboardingPreferences
import com.afoxplus.emergency.presentation.onboarding.OnboardingPreferencesImpl
import com.afoxplus.emergency.presentation.register.RegistrationPreferences
import com.afoxplus.emergency.presentation.register.RegistrationPreferencesImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {
    @Provides
    @Singleton
    fun provideOnboardingPreferences(
        @ApplicationContext context: Context
    ): OnboardingPreferences = OnboardingPreferencesImpl(context)

    @Provides
    @Singleton
    fun provideRegistrationPreferences(
        @ApplicationContext context: Context
    ): RegistrationPreferences = RegistrationPreferencesImpl(context)

    @Provides
    @Singleton
    fun provideContactsRepository(
        @ApplicationContext context: Context
    ): ContactsRepository = ContactsRepositoryImpl(context.contentResolver)

    @Provides
    @Singleton
    fun provideEmergencyContactRepository(
        @ApplicationContext context: Context
    ): EmergencyContactRepository = EmergencyContactRepositoryImpl(context)
}
