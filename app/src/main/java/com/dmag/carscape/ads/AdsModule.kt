package com.dmag.carscape.ads

import com.dmag.carscape.domain.repository.RewardedAdRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdsModule {
    @Binds
    @Singleton
    abstract fun bindRewardAdRepository(impl: RewardedAdRepositoryImpl): RewardedAdRepository
}