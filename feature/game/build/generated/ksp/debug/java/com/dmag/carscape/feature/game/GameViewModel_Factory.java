package com.dmag.carscape.feature.game;

import com.dmag.carscape.core.common.DispatcherProvider;
import com.dmag.carscape.domain.repository.LevelRepository;
import com.dmag.carscape.domain.repository.ProgressRepository;
import com.dmag.carscape.domain.usecase.GetValidSlideDistanceUseCase;
import com.dmag.carscape.domain.usecase.MoveVehicleUseCase;
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
public final class GameViewModel_Factory implements Factory<GameViewModel> {
  private final Provider<LevelRepository> levelRepositoryProvider;

  private final Provider<ProgressRepository> progressRepositoryProvider;

  private final Provider<MoveVehicleUseCase> moveVehicleProvider;

  private final Provider<GetValidSlideDistanceUseCase> getValidSlideDistanceProvider;

  private final Provider<DispatcherProvider> dispatchersProvider;

  private GameViewModel_Factory(Provider<LevelRepository> levelRepositoryProvider,
      Provider<ProgressRepository> progressRepositoryProvider,
      Provider<MoveVehicleUseCase> moveVehicleProvider,
      Provider<GetValidSlideDistanceUseCase> getValidSlideDistanceProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    this.levelRepositoryProvider = levelRepositoryProvider;
    this.progressRepositoryProvider = progressRepositoryProvider;
    this.moveVehicleProvider = moveVehicleProvider;
    this.getValidSlideDistanceProvider = getValidSlideDistanceProvider;
    this.dispatchersProvider = dispatchersProvider;
  }

  @Override
  public GameViewModel get() {
    return newInstance(levelRepositoryProvider.get(), progressRepositoryProvider.get(), moveVehicleProvider.get(), getValidSlideDistanceProvider.get(), dispatchersProvider.get());
  }

  public static GameViewModel_Factory create(Provider<LevelRepository> levelRepositoryProvider,
      Provider<ProgressRepository> progressRepositoryProvider,
      Provider<MoveVehicleUseCase> moveVehicleProvider,
      Provider<GetValidSlideDistanceUseCase> getValidSlideDistanceProvider,
      Provider<DispatcherProvider> dispatchersProvider) {
    return new GameViewModel_Factory(levelRepositoryProvider, progressRepositoryProvider, moveVehicleProvider, getValidSlideDistanceProvider, dispatchersProvider);
  }

  public static GameViewModel newInstance(LevelRepository levelRepository,
      ProgressRepository progressRepository, MoveVehicleUseCase moveVehicle,
      GetValidSlideDistanceUseCase getValidSlideDistance, DispatcherProvider dispatchers) {
    return new GameViewModel(levelRepository, progressRepository, moveVehicle, getValidSlideDistance, dispatchers);
  }
}
