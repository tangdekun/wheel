package com.tdk.daggerdemo

import android.app.Application
import com.blankj.utilcode.util.Utils

/**
 * @Author tangdekun
 * @Date 2018/7/30-14:50
 * @Email tangdekun0924@gmail.com
 */
class MyApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        inject()
    }


    fun inject() {
        var mAppComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        AppComponentHolder.mAppComponent = mAppComponent
    }

}