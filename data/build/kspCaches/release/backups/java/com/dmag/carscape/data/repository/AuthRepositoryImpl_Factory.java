package com.dmag.carscape.data.repository;

import com.dmag.carscape.core.common.DispatcherProvider;
import com.google.firebase.auth.FirebaseAuth;
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<FirebaseAuth> authProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  private AuthRepositoryImpl_Factory(Provider<FirebaseAuth> authProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.authProvider = authProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(authProvider.get(), dispatchersProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<FirebaseAuth> authProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new AuthRepositoryImpl_Factory(authProvider, dispatchersProvider);
  }

  public static AuthRepositoryImpl newInstance(FirebaseAuth auth, DispatcherProvider dispatchers) {
    return new AuthRepositoryImpl(auth, dispatchers);
  }
}
