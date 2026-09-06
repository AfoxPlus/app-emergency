package com.afoxplus.emergency.di

import android.content.Context
import com.afoxplus.emergency.data.repository.ContactsRepositoryImpl
import com.afoxplus.emergency.data.repository.EmergencyContactRepositoryImpl
import com.afoxplus.emergency.data.repository.EmergencyContactsCountProviderImpl
import com.afoxplus.emergency.data.repository.OnboardingPreferencesImpl
import com.afoxplus.emergency.data.repository.PeriodicCheckPreferencesImpl
import com.afoxplus.emergency.data.repository.RegistrationPreferencesImpl
import com.afoxplus.emergency.data.repository.SettingsPreferencesImpl
import com.afoxplus.emergency.domain.repository.ContactsRepository
import com.afoxplus.emergency.domain.repository.EmergencyContactRepository
import com.afoxplus.emergency.domain.repository.EmergencyContactsCountProvider
import com.afoxplus.emergency.domain.repository.OnboardingPreferences
import com.afoxplus.emergency.domain.repository.PeriodicCheckPreferences
import com.afoxplus.emergency.domain.repository.RegistrationPreferences
import com.afoxplus.emergency.domain.repository.SettingsPreferences
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
    fun provideSettingsPreferences(
        @ApplicationContext context: Context
    ): SettingsPreferences = SettingsPreferencesImpl(context)

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

    @Provides
    @Singleton
    fun providePeriodicCheckPreferences(
        @ApplicationContext context: Context
    ): PeriodicCheckPreferences = PeriodicCheckPreferencesImpl(context)

    @Provides
    @Singleton
    fun provideEmergencyContactsCountProvider(
        emergencyContactRepository: EmergencyContactRepository
    ): EmergencyContactsCountProvider = EmergencyContactsCountProviderImpl(emergencyContactRepository)
}
