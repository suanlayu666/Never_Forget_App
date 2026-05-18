package com.neverforget.ui.main;

import com.neverforget.repository.MessageRepository;
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
public final class MessageListViewModel_Factory implements Factory<MessageListViewModel> {
  private final Provider<MessageRepository> messageRepositoryProvider;

  public MessageListViewModel_Factory(Provider<MessageRepository> messageRepositoryProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
  }

  @Override
  public MessageListViewModel get() {
    return newInstance(messageRepositoryProvider.get());
  }

  public static MessageListViewModel_Factory create(
      Provider<MessageRepository> messageRepositoryProvider) {
    return new MessageListViewModel_Factory(messageRepositoryProvider);
  }

  public static MessageListViewModel newInstance(MessageRepository messageRepository) {
    return new MessageListViewModel(messageRepository);
  }
}
