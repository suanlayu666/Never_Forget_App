package com.neverforget.repository;

import com.neverforget.data.local.dao.SummaryDao;
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
public final class SummaryRepository_Factory implements Factory<SummaryRepository> {
  private final Provider<SummaryDao> summaryDaoProvider;

  public SummaryRepository_Factory(Provider<SummaryDao> summaryDaoProvider) {
    this.summaryDaoProvider = summaryDaoProvider;
  }

  @Override
  public SummaryRepository get() {
    return newInstance(summaryDaoProvider.get());
  }

  public static SummaryRepository_Factory create(Provider<SummaryDao> summaryDaoProvider) {
    return new SummaryRepository_Factory(summaryDaoProvider);
  }

  public static SummaryRepository newInstance(SummaryDao summaryDao) {
    return new SummaryRepository(summaryDao);
  }
}
