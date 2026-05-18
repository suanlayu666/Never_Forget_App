package com.neverforget.data.local;

import com.neverforget.data.preferences.AppPreferences;
import com.neverforget.repository.MessageRepository;
import com.neverforget.repository.SummaryRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
    "KotlinInternalInJava"
})
public final class MockDataInitializer_Factory implements Factory<MockDataInitializer> {
  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<SummaryRepository> summaryRepositoryProvider;

  private final Provider<AppPreferences> appPreferencesProvider;

  public MockDataInitializer_Factory(Provider<MessageRepository> messageRepositoryProvider,
      Provider<SummaryRepository> summaryRepositoryProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.summaryRepositoryProvider = summaryRepositoryProvider;
    this.appPreferencesProvider = appPreferencesProvider;
  }

  @Override
  public MockDataInitializer get() {
    return newInstance(messageRepositoryProvider.get(), summaryRepositoryProvider.get(), appPreferencesProvider.get());
  }

  public static MockDataInitializer_Factory create(
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<SummaryRepository> summaryRepositoryProvider,
      Provider<AppPreferences> appPreferencesProvider) {
    return new MockDataInitializer_Factory(messageRepositoryProvider, summaryRepositoryProvider, appPreferencesProvider);
  }

  public static MockDataInitializer newInstance(MessageRepository messageRepository,
      SummaryRepository summaryRepository, AppPreferences appPreferences) {
    return new MockDataInitializer(messageRepository, summaryRepository, appPreferences);
  }
}
