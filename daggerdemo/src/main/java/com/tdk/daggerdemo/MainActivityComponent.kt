package com.tdk.daggerdemo

import dagger.Component

@Component(modules = [MainActivityModule::class])
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity)
}