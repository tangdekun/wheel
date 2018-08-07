package com.tdk.daggerdemo

import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * @Author tangdekun
 * @Date 2018/7/30-10:45
 * @Email tangdekun0924@gmail.com
 */
@Module
class MainModule {

    @Provides
    fun provideStudent(@Named("math") lesson: Lesson): Student {
        return Student("tangdekun", 26, lesson)
    }


    @Provides
    @Named("math")
    fun provideLesson(): Lesson {
        return Lesson("数字", 100)
    }


    @Provides
    @Named("chinese")
    fun provideLesson2(@Named("shuxue") name: String, score: Int): Lesson {
        return Lesson(name, score)
    }

    @Provides
    @Named("english")
    fun provideLessonEnglish(name: String, score: Int): Lesson {
        return Lesson("英语", 150)
    }

    @Provides
    @Named("shuxue")
    fun provideName(): String {
        return "数学"
    }


    @Provides
    @Named("yuwen")
    fun provideYuwenName(): String {
        return "语文"
    }

    @Provides
    fun provideScore(): Int {
        return 90
    }

    @Provides
    fun provideStudent1(@Named("chinese") lesson: Lesson): Student1 {
        return Student1(lesson)
    }

}