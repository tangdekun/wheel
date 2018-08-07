package com.tdk.daggerdemo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.LogUtils
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var student1: Student1
    @Inject
    lateinit var student: Student

    //    @Named("math")
//    @Inject
//    lateinit var lessonMath: Lesson
//
//    @Inject
//    @Named("english")
//    lateinit var englishLesson: Lesson
//    @Inject
//    @English
//    var englishProject: Project? = null

//    @Inject
//    lateinit var lessonInterface: LessonInterface

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DaggerMainComponent.builder()
                .mainModule(MainModule())
                .build()
                .inject(this)
        student1.name = "唐德坤"
        student1.age = 26

//        student1.lesson?.name = "英语"
//        student1.lesson?.score = 100
        daggerdemo_tv.text = student1.toString() + "\n" + student1.lesson.toString() + "\n" + student.lesson.toString()
        LogUtils.dTag("MainActivity", student1.toString(), student.lesson.toString())
//        LogUtils.dTag("MainActivity", englishProject.toString())
//        LogUtils.dTag("MainActivity", lessonMath.toString())
    }
}
