package com.tdk.daggerdemo

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @Author tangdekun
 * @Date 2018/7/30-14:20
 * @Email tangdekun0924@gmail.com
 */
@Module
class AppModule {


    @Singleton
    @Provides
    fun provideAppContext(application: MyApplication): Context {
        return application
    }

    @Singleton
    @Provides
    fun provideSharedPreference(mContext: Context): SharedPreferences {
        return mContext.getSharedPreferences(Constant.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
    }


}