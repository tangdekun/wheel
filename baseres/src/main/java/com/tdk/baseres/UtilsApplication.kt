package com.hp.baseres

import android.content.Context
import utils.CrashUtils
import utils.LogUtils
import com.blankj.utilcode.util.Utils as UtilCodeUtils


class UtilsApplication : BaseApplication() {
    companion object {
        lateinit var mContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this@UtilsApplication
        UtilCodeUtils.init(this)
        initCrash()
        initLog()
    }

    private fun initLog() {
        val config: LogUtils.Config = LogUtils.config
                .setLogSwitch(BuildConfig.DEBUG)// 设置 log 总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(BuildConfig.DEBUG)// 设置是否输出到控制台开关，默认开
                .setGlobalTag(null)// 设置 log 全局标签，默认为空
                // 当全局标签不为空时，我们输出的 log 全部为该 tag，
                // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
                .setLogHeadSwitch(true)// 设置 log 头信息开关，默认为开
                .setLog2FileSwitch(true)// 打印 log 时是否存到文件的开关，默认关
                .setDir(null)// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("")// 当文件前缀为空时，默认为"util"，即写入文件为"util-MM-dd.txt"
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setSingleTagSwitch(true)// 一条日志仅输出一条，默认开，为美化 AS 3.1 的 Logcat
                .setConsoleFilter(LogUtils.V)// log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
                .setFileFilter(LogUtils.V)// log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
                .setStackDeep(1)// log 栈深度，默认为 1
                //默认在Release模式下是不打印log的,但是为了方便在用户使用应用过程中出现异常进行定位,在sdcard中新建一个
                //LogInfos文件夹就可以打印在LogInfos文件夹中
                .setWriteLogForRelease(true)

//        ThreadPoolExecutorUtil.setMaxSize(20)
//                .setWorkQueue(LinkedBlockingQueue(100))
//                .build().execute {
//                    LogUtils.d(config)
//                    LogUtils.dTag("CurrentThread:name = ${Thread.currentThread().name},id = ${Thread.currentThread().id}")
//                }


    }

    private fun initCrash() {
        CrashUtils.init()
    }


}