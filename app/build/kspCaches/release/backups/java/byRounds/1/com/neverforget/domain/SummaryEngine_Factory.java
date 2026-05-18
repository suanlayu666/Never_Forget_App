package com.neverforget.domain;

import com.neverforget.data.preferences.AppPreferences;
import com.neverforget.repository.MessageRepository;
import com.neverforget.repository.SummaryRepository;
import com.neverforget.util.NotificationHelper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class SummaryEngine_Factory implements Factory<SummaryEngine> {
  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<SummaryRepository> summaryRepositoryProvider;

  private final Provider<AppPreferences> appPreferencesProvider;

  private final Provider<OkHttpClient> okHttpClientProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  public SummaryEngine_Factory(Provider<MessageRepository> messageRepositoryProvider,
      Provider<SummaryRepository> summaryRepositoryProvider,
      Provider<AppPreferences> appPreferencesProvider, Provider<OkHttpClient> okHttpClientProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.summaryRepositoryProvider = summaryRepositoryProvider;
    this.appPreferencesProvider = appPreferencesProvider;
    this.okHttpClientProvider = okHttpClientProvider;
    this.notificationHelperProvider = notificationHelperProvider;
  }

  @Override
  public SummaryEngine get() {
    return newInstance(messageRepositoryProvider.get(), summaryRepositoryProvider.get(), appPreferencesProvider.get(), okHttpClientProvider.get(), notificationHelperProvider.get());
  }

  public static SummaryEngine_Factory create(Provider<MessageRepository> messageRepositoryProvider,
      Provider<SummaryRepository> summaryRepositoryProvider,
      Provider<AppPreferences> appPreferencesProvider, Provider<OkHttpClient> okHttpClientProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    return new SummaryEngine_Factory(messageRepositoryProvider, summaryRepositoryProvider, appPreferencesProvider, okHttpClientProvider, notificationHelperProvider);
  }

  public static SummaryEngine newInstance(MessageRepository messageRepository,
      SummaryRepository summaryRepository, AppPreferences appPreferences, OkHttpClient okHttpClient,
      NotificationHelper notificationHelper) {
    return new SummaryEngine(messageRepository, summaryRepository, appPreferences, okHttpClient, notificationHelper);
  }
}
