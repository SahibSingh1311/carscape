package com.dmag.carscape.data.repository;

import android.content.Context;
import com.dmag.carscape.core.common.DispatcherProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class LevelRepositoryImpl_Factory implements Factory<LevelRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  private LevelRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.contextProvider = contextProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public LevelRepositoryImpl get() {
    return newInstance(contextProvider.get(), dispatchersProvider.get());
  }

  public static LevelRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new LevelRepositoryImpl_Factory(contextProvider, dispatchersProvider);
  }

  public static LevelRepositoryImpl newInstance(Context context, DispatcherProvider dispatchers) {
    return new LevelRepositoryImpl(context, dispatchers);
  }
}
