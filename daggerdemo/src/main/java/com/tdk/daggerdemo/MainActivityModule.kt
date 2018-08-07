package com.tdk.daggerdemo

import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class MainActivityModule constructor(mainActivity: MainActivity) {

    private val mainActivity = mainActivity

    /**
     * 实际年龄
     */
    @Named("shisui")
    @Provides
    fun provideStudent(): Student {
        return Student("tangdekun", 25)
    }

    /**
     * 虚岁
     */
    @Named("xusui")
    @Provides
    fun providerStudent(): Student {
        return Student("tangdekun", 26)
    }


}