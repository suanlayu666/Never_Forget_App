package com.neverforget.ui.settings;

import com.neverforget.data.preferences.AppPreferences;
import com.neverforget.repository.MessageRepository;
import com.neverforget.repository.SummaryRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
    "KotlinInternalInJava"
})
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<AppPreferences> appPreferencesProvider;

  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<SummaryRepository> summaryRepositoryProvider;

  public SettingsViewModel_Factory(Provider<AppPreferences> appPreferencesProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<SummaryRepository> summaryRepositoryProvider) {
    this.appPreferencesProvider = appPreferencesProvider;
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.summaryRepositoryProvider = summaryRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(appPreferencesProvider.get(), messageRepositoryProvider.get(), summaryRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<AppPreferences> appPreferencesProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<SummaryRepository> summaryRepositoryProvider) {
    return new SettingsViewModel_Factory(appPreferencesProvider, messageRepositoryProvider, summaryRepositoryProvider);
  }

  public static SettingsViewModel newInstance(AppPreferences appPreferences,
      MessageRepository messageRepository, SummaryRepository summaryRepository) {
    return new SettingsViewModel(appPreferences, messageRepository, summaryRepository);
  }
}
