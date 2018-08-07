package com.tdk.daggerdemo;

import dagger.Module;
import dagger.Provides;

@Module
public class ProjectModule {

    @Provides
    @English
    public Project provideProjectEnglish() {
        return new Project(122, "English");
    }

    @Provides
    @Math
    public Project provideProjectMath() {
        return new Project(122, "Math");
    }

}
