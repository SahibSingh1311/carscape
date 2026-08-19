package com.dmag.carscape.data.repository;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.dmag.carscape.core.common.DispatcherProvider;
import com.dmag.carscape.domain.repository.AuthRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
public final class WalletRepositoryImpl_Factory implements Factory<WalletRepositoryImpl> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  private WalletRepositoryImpl_Factory(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.dataStoreProvider = dataStoreProvider;
    this.firestoreProvider = firestoreProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public WalletRepositoryImpl get() {
    return newInstance(dataStoreProvider.get(), firestoreProvider.get(), authRepositoryProvider.get(), dispatchersProvider.get());
  }

  public static WalletRepositoryImpl_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<AuthRepository> authRepositoryProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new WalletRepositoryImpl_Factory(dataStoreProvider, firestoreProvider, authRepositoryProvider, dispatchersProvider);
  }

  public static WalletRepositoryImpl newInstance(DataStore<Preferences> dataStore,
      FirebaseFirestore firestore, AuthRepository authRepository, DispatcherProvider dispatchers) {
    return new WalletRepositoryImpl(dataStore, firestore, authRepository, dispatchers);
  }
}
