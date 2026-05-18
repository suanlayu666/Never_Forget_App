package com.neverforget.floating;

import com.neverforget.domain.SummaryEngine;
import com.neverforget.repository.MessageRepository;
import com.neverforget.repository.SummaryRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class FloatingWindowService_MembersInjector implements MembersInjector<FloatingWindowService> {
  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<SummaryRepository> summaryRepositoryProvider;

  private final Provider<SummaryEngine> summaryEngineProvider;

  public FloatingWindowService_MembersInjector(
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<SummaryRepository> summaryRepositoryProvider,
      Provider<SummaryEngine> summaryEngineProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.summaryRepositoryProvider = summaryRepositoryProvider;
    this.summaryEngineProvider = summaryEngineProvider;
  }

  public static MembersInjector<FloatingWindowService> create(
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<SummaryRepository> summaryRepositoryProvider,
      Provider<SummaryEngine> summaryEngineProvider) {
    return new FloatingWindowService_MembersInjector(messageRepositoryProvider, summaryRepositoryProvider, summaryEngineProvider);
  }

  @Override
  public void injectMembers(FloatingWindowService instance) {
    injectMessageRepository(instance, messageRepositoryProvider.get());
    injectSummaryRepository(instance, summaryRepositoryProvider.get());
    injectSummaryEngine(instance, summaryEngineProvider.get());
  }

  @InjectedFieldSignature("com.neverforget.floating.FloatingWindowService.messageRepository")
  public static void injectMessageRepository(FloatingWindowService instance,
      MessageRepository messageRepository) {
    instance.messageRepository = messageRepository;
  }

  @InjectedFieldSignature("com.neverforget.floating.FloatingWindowService.summaryRepository")
  public static void injectSummaryRepository(FloatingWindowService instance,
      SummaryRepository summaryRepository) {
    instance.summaryRepository = summaryRepository;
  }

  @InjectedFieldSignature("com.neverforget.floating.FloatingWindowService.summaryEngine")
  public static void injectSummaryEngine(FloatingWindowService instance,
      SummaryEngine summaryEngine) {
    instance.summaryEngine = summaryEngine;
  }
}
