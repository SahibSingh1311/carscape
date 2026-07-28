package com.dmag.carscape.data.repository;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
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
public final class ProgressRepositoryImpl_Factory implements Factory<ProgressRepositoryImpl> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private ProgressRepositoryImpl_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public ProgressRepositoryImpl get() {
    return newInstance(dataStoreProvider.get());
  }

  public static ProgressRepositoryImpl_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new ProgressRepositoryImpl_Factory(dataStoreProvider);
  }

  public static ProgressRepositoryImpl newInstance(DataStore<Preferences> dataStore) {
    return new ProgressRepositoryImpl(dataStore);
  }
}
