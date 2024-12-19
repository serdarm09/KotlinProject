package com.ornek.cartrackingsystem.di

import com.ornek.cartrackingsystem.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun bindAuthRepository(auth: FirebaseAuth): AuthRepository = AuthRepository(auth)
}