package com.neverforget.domain;

import com.neverforget.repository.MessageRepository;
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
public final class MessageCaptureManager_Factory implements Factory<MessageCaptureManager> {
  private final Provider<MessageRepository> messageRepositoryProvider;

  public MessageCaptureManager_Factory(Provider<MessageRepository> messageRepositoryProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
  }

  @Override
  public MessageCaptureManager get() {
    return newInstance(messageRepositoryProvider.get());
  }

  public static MessageCaptureManager_Factory create(
      Provider<MessageRepository> messageRepositoryProvider) {
    return new MessageCaptureManager_Factory(messageRepositoryProvider);
  }

  public static MessageCaptureManager newInstance(MessageRepository messageRepository) {
    return new MessageCaptureManager(messageRepository);
  }
}
