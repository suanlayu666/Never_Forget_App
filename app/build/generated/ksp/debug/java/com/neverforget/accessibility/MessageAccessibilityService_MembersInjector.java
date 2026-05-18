package com.neverforget.accessibility;

import com.neverforget.repository.MessageRepository;
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
public final class MessageAccessibilityService_MembersInjector implements MembersInjector<MessageAccessibilityService> {
  private final Provider<MessageRepository> messageRepositoryProvider;

  public MessageAccessibilityService_MembersInjector(
      Provider<MessageRepository> messageRepositoryProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
  }

  public static MembersInjector<MessageAccessibilityService> create(
      Provider<MessageRepository> messageRepositoryProvider) {
    return new MessageAccessibilityService_MembersInjector(messageRepositoryProvider);
  }

  @Override
  public void injectMembers(MessageAccessibilityService instance) {
    injectMessageRepository(instance, messageRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.neverforget.accessibility.MessageAccessibilityService.messageRepository")
  public static void injectMessageRepository(MessageAccessibilityService instance,
      MessageRepository messageRepository) {
    instance.messageRepository = messageRepository;
  }
}
