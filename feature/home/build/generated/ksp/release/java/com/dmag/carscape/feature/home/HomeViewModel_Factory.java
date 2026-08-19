package com.dmag.carscape.feature.home;

import com.dmag.carscape.domain.repository.ProgressRepository;
import com.dmag.carscape.domain.repository.WalletRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<ProgressRepository> progressRepositoryProvider;

  private final Provider<WalletRepository> walletRepositoryProvider;

  private HomeViewModel_Factory(Provider<ProgressRepository> progressRepositoryProvider,
      Provider<WalletRepository> walletRepositoryProvider) {
    this.progressRepositoryProvider = progressRepositoryProvider;
    this.walletRepositoryProvider = walletRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(progressRepositoryProvider.get(), walletRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<ProgressRepository> progressRepositoryProvider,
      Provider<WalletRepository> walletRepositoryProvider) {
    return new HomeViewModel_Factory(progressRepositoryProvider, walletRepositoryProvider);
  }

  public static HomeViewModel newInstance(ProgressRepository progressRepository,
      WalletRepository walletRepository) {
    return new HomeViewModel(progressRepository, walletRepository);
  }
}
