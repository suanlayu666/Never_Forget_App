package com.neverforget.ui.summary;

import com.neverforget.domain.SummaryEngine;
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
public final class SummaryViewModel_Factory implements Factory<SummaryViewModel> {
  private final Provider<SummaryRepository> summaryRepositoryProvider;

  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<SummaryEngine> summaryEngineProvider;

  public SummaryViewModel_Factory(Provider<SummaryRepository> summaryRepositoryProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<SummaryEngine> summaryEngineProvider) {
    this.summaryRepositoryProvider = summaryRepositoryProvider;
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.summaryEngineProvider = summaryEngineProvider;
  }

  @Override
  public SummaryViewModel get() {
    return newInstance(summaryRepositoryProvider.get(), messageRepositoryProvider.get(), summaryEngineProvider.get());
  }

  public static SummaryViewModel_Factory create(
      Provider<SummaryRepository> summaryRepositoryProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<SummaryEngine> summaryEngineProvider) {
    return new SummaryViewModel_Factory(summaryRepositoryProvider, messageRepositoryProvider, summaryEngineProvider);
  }

  public static SummaryViewModel newInstance(SummaryRepository summaryRepository,
      MessageRepository messageRepository, SummaryEngine summaryEngine) {
    return new SummaryViewModel(summaryRepository, messageRepository, summaryEngine);
  }
}
