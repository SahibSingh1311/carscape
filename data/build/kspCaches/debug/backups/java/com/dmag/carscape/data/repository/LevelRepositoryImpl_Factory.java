package com.dmag.carscape.data.repository;

import com.dmag.carscape.core.common.DispatcherProvider;
import com.google.firebase.firestore.FirebaseFirestore;
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
public final class LevelRepositoryImpl_Factory implements Factory<LevelRepositoryImpl> {
  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  private LevelRepositoryImpl_Factory(Provider<FirebaseFirestore> firestoreProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.firestoreProvider = firestoreProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public LevelRepositoryImpl get() {
    return newInstance(firestoreProvider.get(), dispatchersProvider.get());
  }

  public static LevelRepositoryImpl_Factory create(Provider<FirebaseFirestore> firestoreProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new LevelRepositoryImpl_Factory(firestoreProvider, dispatchersProvider);
  }

  public static LevelRepositoryImpl newInstance(FirebaseFirestore firestore,
      DispatcherProvider dispatchers) {
    return new LevelRepositoryImpl(firestore, dispatchers);
  }
}
