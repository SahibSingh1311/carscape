package com.dmag.carscape.data.di;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class DataStoreModule_ProvideRemoteConfigFactory implements Factory<FirebaseRemoteConfig> {
  @Override
  public FirebaseRemoteConfig get() {
    return provideRemoteConfig();
  }

  public static DataStoreModule_ProvideRemoteConfigFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseRemoteConfig provideRemoteConfig() {
    return Preconditions.checkNotNullFromProvides(DataStoreModule.INSTANCE.provideRemoteConfig());
  }

  private static final class InstanceHolder {
    static final DataStoreModule_ProvideRemoteConfigFactory INSTANCE = new DataStoreModule_ProvideRemoteConfigFactory();
  }
}
