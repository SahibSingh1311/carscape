package com.dmag.carscape.feature.game.audio;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class GameSoundPlayer_Factory implements Factory<GameSoundPlayer> {
  private final Provider<Context> contextProvider;

  private GameSoundPlayer_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public GameSoundPlayer get() {
    return newInstance(contextProvider.get());
  }

  public static GameSoundPlayer_Factory create(Provider<Context> contextProvider) {
    return new GameSoundPlayer_Factory(contextProvider);
  }

  public static GameSoundPlayer newInstance(Context context) {
    return new GameSoundPlayer(context);
  }
}
