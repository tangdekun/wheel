package com.tdk.daggerdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.daggerdemo_activity_main.*
import javax.inject.Inject
import javax.inject.Named

class MainActivity : AppCompatActivity() {

    @Named("shisui")
    @Inject
    lateinit var student: Student

    @Named("xusui")
    @Inject
    lateinit var xStudent: Student

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.daggerdemo_activity_main)
        DaggerMainActivityComponent.builder()
                .mainActivityModule(MainActivityModule(this))
                .build()
                .inject(this)
        dagger_demo_tv.text = student.toString() + xStudent.toString()


    }
}
