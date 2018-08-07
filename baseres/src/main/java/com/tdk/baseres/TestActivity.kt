package com.hp.baseres

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.StringUtils
import utils.LogUtils
import java.io.File

/**
 * @Author tangdekun
 * @Date 2018/7/20-11:48
 * @Email tangdekun0924@gmail.com
 */
class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.baseres_activity_test)
        var defaultDir: String?
        val crashName = "CrashInfos"
        var crashFile = File(Environment.getExternalStorageDirectory().path + "/${crashName}/")
        if (crashFile.exists()) {
            var appName = AppUtils.getAppName()
            if (!StringUtils.isTrimEmpty(appName)) {
                var crashFileByLable = File("${Environment.getExternalStorageDirectory().path}/${crashName}/${appName}/")
                if (!crashFileByLable.exists()) {
                    if (crashFileByLable.mkdirs()) {
                        defaultDir = crashFileByLable.absolutePath
                    }
                } else {
                    defaultDir = crashFileByLable.absolutePath
                }
            } else {
                defaultDir = crashFile.absolutePath
            }
            LogUtils.dTag("TestActivity", "sdcard:$", "logFile:${crashFile.absolutePath}")
        }
    }
}