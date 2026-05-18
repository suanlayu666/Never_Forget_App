package com.neverforget;

import com.neverforget.data.local.MockDataInitializer;
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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<MockDataInitializer> mockDataInitializerProvider;

  public MainActivity_MembersInjector(Provider<MockDataInitializer> mockDataInitializerProvider) {
    this.mockDataInitializerProvider = mockDataInitializerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<MockDataInitializer> mockDataInitializerProvider) {
    return new MainActivity_MembersInjector(mockDataInitializerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectMockDataInitializer(instance, mockDataInitializerProvider.get());
  }

  @InjectedFieldSignature("com.neverforget.MainActivity.mockDataInitializer")
  public static void injectMockDataInitializer(MainActivity instance,
      MockDataInitializer mockDataInitializer) {
    instance.mockDataInitializer = mockDataInitializer;
  }
}
