package com.dmag.carscape.data.repository;

import com.dmag.carscape.core.common.DispatcherProvider;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class PricingRepositoryImpl_Factory implements Factory<PricingRepositoryImpl> {
  private final Provider<FirebaseRemoteConfig> remoteConfigProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  private PricingRepositoryImpl_Factory(Provider<FirebaseRemoteConfig> remoteConfigProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.remoteConfigProvider = remoteConfigProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public PricingRepositoryImpl get() {
    return newInstance(remoteConfigProvider.get(), dispatchersProvider.get());
  }

  public static PricingRepositoryImpl_Factory create(
      Provider<FirebaseRemoteConfig> remoteConfigProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new PricingRepositoryImpl_Factory(remoteConfigProvider, dispatchersProvider);
  }

  public static PricingRepositoryImpl newInstance(FirebaseRemoteConfig remoteConfig,
      DispatcherProvider dispatchers) {
    return new PricingRepositoryImpl(remoteConfig, dispatchers);
  }
}
