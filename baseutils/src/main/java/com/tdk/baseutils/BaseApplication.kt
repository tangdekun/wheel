//package com.hp.baseutils
//
//import android.app.Activity
//import android.app.Application
//import android.content.Context
//import android.content.Intent
//import android.content.res.Configuration
//import android.util.Log
//
//import com.hp.bookcapture.util.BitmapUtilsHelp
//import com.hp.bookcapture.util.SharedPreferencesUtil
//import com.hp.floatwindow.AutoStartService
//import com.hp.floatwindow.FloatWindowManager
//import com.hp.robot.util.CrashHandler
//import com.hp.robot.util.ImageLoaderUtil
//import com.iflytek.cloud.SpeechUtility
//import com.netease.scan.QrScan
//import com.netease.scan.QrScanConfiguration
//import com.nostra13.universalimageloader.core.ImageLoader
//
//import java.util.ArrayList
//
///**
// * @author tangdekun
// * @date 2017/3/11
// */
//
//class BaseApplication : Application() {
//    var activitieList: MutableList<Activity>? = null
//
//    override fun onCreate() {
//        initSpeech()
//        super.onCreate()
//
//        Log.d(TAG, System.currentTimeMillis().toString() + "")
//        setCrashHandler()
//        activitieList = ArrayList()
//        context = this@BaseApplication
//        ImageLoader.getInstance().init(ImageLoaderUtil.initImageLoaderConfig(context))
//        //      QrScanConfiguration configuration = QrScanConfiguration.createDefault(this);
//        BitmapUtilsHelp.init(this).clearDiskCache()
//        // 自定义配置
//        val configuration = QrScanConfiguration.Builder(this)
//                .setTitleHeight(53)
//                .setTitleText("来扫一扫")
//                .setTitleTextSize(18)
//                .setTitleTextColor(R.color.white)
//                .setTipText("将书本放入框内扫描~")
//                .setTipTextSize(14)
//                .setTipMarginTop(40)
//                .setTipTextColor(R.color.white)
//                .setSlideIcon(R.mipmap.capture_add_scanning)
//                .setAngleColor(R.color.white)
//                .setMaskColor(R.color.black_80)
//                .setScanFrameRectRate(0.8.toFloat())
//                .build()
//        QrScan.getInstance().init(configuration)
//        //        System.loadLibrary(getApplicationInfo().nativeLibraryDir+"libBaiduSpeechSDK.so");
//        val service = Intent(this, AutoStartService::class.java)
//        this.startService(service)
//        SharedPreferencesUtil.getInstance().saveData(Constant.SharedPreferencesConstant.SP_KEY_WAKEUP_AVAIABLE, true)
//        Log.d(TAG, "onCreate: SharedPreferencesUtil")
//        Log.d(TAG, System.currentTimeMillis().toString() + "")
//
//    }
//
//    private fun initSpeech() {
//        // 应用程序入口处调用，避免手机内存过小，杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
//        // 如在Application中调用初始化，需要在Mainifest中注册该Applicaiton
//        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
//        // 参数间使用半角“,”分隔。
//        // 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符
//
//        // 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
//
//        SpeechUtility.createUtility(this, "appid=58dc9fc5")
//        // 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
//        // Setting.setShowLog(false);
//    }
//
//    /**
//     * 初始化Crash信息的收集java.lang.String
//     */
//    private fun setCrashHandler() {
//        CrashHandler.getInstance(this)
//    }
//
//    /**
//     * 将Activity加入Activity列表中
//     *
//     * @param activity
//     */
//    fun addActivity(activity: Activity) {
//        if (activitieList != null && activitieList!!.size > 0) {
//            if (!activitieList!!.contains(activity)) {
//                activitieList!!.add(activity)
//            }
//        } else {
//            activitieList!!.add(activity)
//        }
//    }
//
//    /**
//     * 将指定Activity移除Activity列表中
//     *
//     * @param activity
//     */
//    fun removeActivity(activity: Activity) {
//        if (activitieList != null && activitieList!!.size > 0) {
//            if (activitieList!!.contains(activity)) {
//                activitieList!!.remove(activity)
//            }
//        }
//    }
//
//    /**
//     * 关闭所有的Activity
//     */
//    fun closeAllActivity() {
//        for (activity in activitieList!!) {
//            activity.finish()
//        }
//
//    }
//
//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        //// TODO: 2018/6/23  只有横竖屏切换的时候  更新浮窗位置
//        if (!(oldW == newConfig.screenWidthDp && oldH == newConfig.screenHeightDp)) {
//            oldW = newConfig.screenWidthDp
//            oldH = newConfig.screenHeightDp
//            FloatWindowManager.resetSmallWindowPosition(this)
//        }
//    }
//
//    companion object {
//        private val TAG = "BaseApplication"
//        lateinit var context: Context
//        var isGirl = false
//        var isXunFei = false
//
//        var mCurrentTime: Long = 0
//        /**
//         * 当前的模式：主动和手动模式
//         */
//        var mCurrentMode = Constant.RobotSpeechConstant.MODEL_AUTO
//        /**
//         * 当前的语言：中文和英文
//         */
//
//        var mCurrentLanguageMode = Constant.RobotSpeechConstant.MODEL_CHINESE
//        /**
//         * 是否需要切换中文和英文
//         */
//        var isChangeLanguage = false
//        /**
//         * 小易的当前发音音色
//         */
//        var mCurrentTone = Constant.RobotSpeechConstant.robotTones[0]
//        /**
//         * 默认是可以语音识别，但是在执行onPause方法是设置为false,onResume方法设置为true
//         */
//        var isAllowToRecord = true
//
//        /**
//         * 秘钥
//         */
//        val SERCET_KEY = "znkjsr --29269181-7aaa-4d88-a4f2-fdf2aa105fab  "
//        var isNeedSwitchTTSEngine = true
//
//        /**
//         * tts切换在线离线引擎是否成功
//         */
//        var isSwitchTTSEngine = false
//        /**
//         * 初始化TTS是否成功
//         */
//        var isInitTTSEngine = false
//        /**
//         * 更新词典库成功
//         */
//        var isLexiconUpdatedSucccess = false
//        /**
//         * 更新联系人和应用列表是否成功
//         */
//        var isUploadAppsAndContacts = false
//        /**
//         * 是否在切换歌曲
//         */
//        var isChangeSounds = false
//        /**
//         * 是否在切换歌曲
//         */
//        var isGameTTS = false
//        /**
//         * 是否被打断：当点击播放视频/打开学习拍/开心玩的时候 isInterrupt置为true
//         * 当被打断的时候，在语音听写/语义理解/TTS发音等环节，停止当前操作和下一个环节的操作
//         */
//        var isInterrupt = false
//
//        var oldW: Int = 0
//        var oldH: Int = 0
//    }
//}
