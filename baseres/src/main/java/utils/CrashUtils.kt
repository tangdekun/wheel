/**
 * @Author tangdekun
 * @Date 2018/7/20-16:07
 * @Email tangdekun0924@gmail.com
 */
package utils

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresPermission
import android.util.Log
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.Utils
import java.io.*
import java.lang.Thread.UncaughtExceptionHandler
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/09/27
 * desc  : utils about crash
</pre> *
 */
class CrashUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    interface OnCrashListener {
        fun onCrash(crashInfo: String, e: Throwable?)
    }

    companion object {

        private var defaultDir: String? = null
        private var dir: String? = null
        private var versionName: String? = null
        private var versionCode: Int = 0
        private var packageName: String? = null

        private val FILE_SEP = System.getProperty("file.separator")
        @SuppressLint("SimpleDateFormat")
        private val FORMAT = SimpleDateFormat("yyyy-MM-dd HH-mm-ss")

        private val DEFAULT_UNCAUGHT_EXCEPTION_HANDLER: UncaughtExceptionHandler?
        private val UNCAUGHT_EXCEPTION_HANDLER: UncaughtExceptionHandler

        private var sOnCrashListener: OnCrashListener? = null

        init {
            try {
                val pi = Utils.getApp()
                        .packageManager
                        .getPackageInfo(Utils.getApp().packageName, 0)
                if (pi != null) {
                    versionName = pi.versionName
                    versionCode = pi.versionCode
                    packageName = pi.packageName
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = Thread.getDefaultUncaughtExceptionHandler()

            UNCAUGHT_EXCEPTION_HANDLER = UncaughtExceptionHandler { t, e ->
                if (e == null) {
                    if (DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null) {
                        DEFAULT_UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(t, null)
                    } else {
                        android.os.Process.killProcess(android.os.Process.myPid())
                        System.exit(1)
                    }
                    return@UncaughtExceptionHandler
                }

                val time = FORMAT.format(Date(System.currentTimeMillis()))
                val sb = StringBuilder()
                val head = "************* Log Head ****************" +
                        "\nTime Of Crash      : " + time +
                        "\nDevice Manufacturer: " + Build.MANUFACTURER +
                        "\nDevice Model       : " + Build.MODEL +
                        "\nAndroid Version    : " + Build.VERSION.RELEASE +
                        "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                        "\nApp VersionName    : " + versionName +
                        "\nApp VersionCode    : " + versionCode +
                        "\n************* Log Head ****************\n\n"
                sb.append(head)
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                e.printStackTrace(pw)
                var cause: Throwable? = e.cause
                while (cause != null) {
                    cause.printStackTrace(pw)
                    cause = cause.cause
                }
                pw.flush()
                sb.append(sw.toString())
                val crashInfo = sb.toString()
                val fullPath = (if (dir == null) defaultDir else dir) + packageName + time + ".txt"
                if (createOrExistsFile(fullPath)) {
                    input2File(crashInfo, fullPath)
                } else {
                    Log.e("CrashUtils", "create $fullPath failed!")
                }

                if (sOnCrashListener != null) {
                    sOnCrashListener!!.onCrash(crashInfo, e)
                }

                DEFAULT_UNCAUGHT_EXCEPTION_HANDLER?.uncaughtException(t, e)
            }
        }

        /**
         * Initialization
         *
         * Must hold
         * `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param crashDir The directory of saving crash information.
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        fun init(crashDir: File) {
            init(crashDir.absolutePath, null)
        }

        /**
         * Initialization
         *
         * Must hold
         * `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param onCrashListener The crash listener.
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        fun init(onCrashListener: OnCrashListener) {
            init("", onCrashListener)
        }

        /**
         * Initialization
         *
         * Must hold
         * `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param crashDir        The directory of saving crash information.
         * @param onCrashListener The crash listener.
         */
        @RequiresPermission(WRITE_EXTERNAL_STORAGE)
        fun init(crashDir: File, onCrashListener: OnCrashListener) {
            init(crashDir.absolutePath, onCrashListener)
        }

        /**
         * Initialization
         *
         * Must hold
         * `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
         *
         * @param crashDirPath    The directory's path of saving crash information.
         * @param onCrashListener The crash listener.
         */
        @JvmOverloads
        fun init(crashDirPath: String = "", onCrashListener: OnCrashListener? = null) {
            if (isSpace(crashDirPath)) {
                dir = null
            } else {
                dir = if (crashDirPath.endsWith(FILE_SEP)) crashDirPath else crashDirPath + FILE_SEP
            }
            val crashName = "CrashInfos"
            var crashFile = File(Environment.getExternalStorageDirectory().path + "/${crashName}/")
            if (crashFile.exists()) {
                var appName = AppUtils.getAppName()
                if (!StringUtils.isTrimEmpty(appName)) {
                    var crashFileByLable = File(Environment.getExternalStorageDirectory().path + "/${crashName}/${appName}/")
                    if (!crashFileByLable.exists()) {
                        if (crashFileByLable.mkdirs()) {
                            defaultDir = crashFileByLable.absolutePath + FILE_SEP
                        }
                    } else {
                        defaultDir = crashFileByLable.absolutePath + FILE_SEP
                    }
                } else {
                    defaultDir = crashFile.absolutePath
                }
            }
            sOnCrashListener = onCrashListener

            Thread.setDefaultUncaughtExceptionHandler(UNCAUGHT_EXCEPTION_HANDLER)
        }

        private fun input2File(input: String, filePath: String) {
            val submit = Executors.newSingleThreadExecutor().submit(Callable {
                var bw: BufferedWriter? = null
                try {
                    bw = BufferedWriter(FileWriter(filePath, true))
                    bw.write(input)
                    return@Callable true
                } catch (e: IOException) {
                    e.printStackTrace()
                    return@Callable false
                } finally {
                    try {
                        bw?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            })
            try {
                if (submit.get()) return
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }

            Log.e("CrashUtils", "write crash info to $filePath failed!")
        }

        private fun createOrExistsFile(filePath: String): Boolean {
            val file = File(filePath)
            if (file.exists()) return file.isFile
            if (!createOrExistsDir(file.parentFile)) return false
            try {
                return file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

        }

        private fun createOrExistsDir(file: File?): Boolean {
            return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
        }

        private fun isSpace(s: String?): Boolean {
            if (s == null) return true
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }
    }
}
/**
 * Initialization.
 *
 * Must hold
 * `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
 */
/**
 * Initialization
 *
 * Must hold
 * `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />`
 *
 * @param crashDirPath The directory's path of saving crash information.
 */




