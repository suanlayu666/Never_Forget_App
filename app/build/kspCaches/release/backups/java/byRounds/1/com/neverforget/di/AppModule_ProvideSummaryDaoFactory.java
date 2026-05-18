package com.neverforget.di;

import com.neverforget.data.local.AppDatabase;
import com.neverforget.data.local.dao.SummaryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideSummaryDaoFactory implements Factory<SummaryDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideSummaryDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SummaryDao get() {
    return provideSummaryDao(databaseProvider.get());
  }

  public static AppModule_ProvideSummaryDaoFactory create(Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideSummaryDaoFactory(databaseProvider);
  }

  public static SummaryDao provideSummaryDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSummaryDao(database));
  }
}
