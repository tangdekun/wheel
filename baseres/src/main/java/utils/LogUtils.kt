package utils

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.support.annotation.IntDef
import android.support.annotation.IntRange
import android.util.Log
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.Utils
import com.hp.baseres.UtilsApplication
import com.hp.baseres.UtilsApplication.Companion.mContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/09/21
 * desc  : utils about log
</pre> *
 */
class LogUtils private constructor() {

    @IntDef(V, D, I, W, E, A)
    @Retention(RetentionPolicy.SOURCE)
    annotation class TYPE

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    class Config {
        var mDefaultDir: String? = null// The default storage directory of log.
        var mDir: String? = null       // The storage directory of log.
        var mFilePrefix = "util"// The file prefix of log.
        var mLogSwitch = true  // The switch of log.
        var mLog2ConsoleSwitch = true  // The logcat's switch of log.
        var mGlobalTag: String? = null  // The global tag of log.
        var mTagIsSpace = true  // The global tag is space.
        var mLogHeadSwitch = true  // The head's switch of log.
        var mLog2FileSwitch = false // The file's switch of log.
        var mLogBorderSwitch = true  // The border's switch of log.
        var mSingleTagSwitch = true  // The single tag of log.
        var mConsoleFilter = V     // The console's filter of log.
        var mFileFilter = V     // The file's filter of log.
        var mStackDeep = 1     // The stack's deep of log.
        var mStackOffset = 0     // The stack's offset of log.

        init {
            if (mDefaultDir == null) {
                if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && Utils.getApp().externalCacheDir != null)
                    mDefaultDir = Utils.getApp().externalCacheDir.toString() + FILE_SEP + "log" + FILE_SEP
                else {
                    mDefaultDir = Utils.getApp().cacheDir.toString() + FILE_SEP + "log" + FILE_SEP
                }
            }
        }

        fun setLogSwitch(logSwitch: Boolean): Config {
            mLogSwitch = logSwitch
            return this
        }

        fun setConsoleSwitch(consoleSwitch: Boolean): Config {
            mLog2ConsoleSwitch = consoleSwitch
            return this
        }

        fun setGlobalTag(tag: String?): Config {
            if (isSpace(tag)) {
                mGlobalTag = ""
                mTagIsSpace = true
            } else {
                mGlobalTag = tag
                mTagIsSpace = false
            }
            return this
        }

        fun setLogHeadSwitch(logHeadSwitch: Boolean): Config {
            mLogHeadSwitch = logHeadSwitch
            return this
        }

        fun setLog2FileSwitch(log2FileSwitch: Boolean): Config {
            mLog2FileSwitch = log2FileSwitch
            return this
        }

        fun setDir(dir: String): Config {
            if (isSpace(dir)) {
                mDir = null
            } else {
                mDir = if (dir.endsWith(FILE_SEP)) dir else dir + FILE_SEP
            }
            return this
        }

        fun setDir(dir: File?): Config {
            mDir = if (dir == null) null else dir.absolutePath + FILE_SEP
            return this
        }

        fun setFilePrefix(filePrefix: String): Config {
            if (isSpace(filePrefix)) {
                mFilePrefix = "util"
            } else {
                mFilePrefix = filePrefix
            }
            return this
        }

        fun setBorderSwitch(borderSwitch: Boolean): Config {
            mLogBorderSwitch = borderSwitch
            return this
        }

        fun setSingleTagSwitch(singleTagSwitch: Boolean): Config {
            mSingleTagSwitch = singleTagSwitch
            return this
        }

        fun setConsoleFilter(@TYPE consoleFilter: Int): Config {
            mConsoleFilter = consoleFilter
            return this
        }

        fun setFileFilter(@TYPE fileFilter: Int): Config {
            mFileFilter = fileFilter
            return this
        }

        fun setStackDeep(@IntRange(from = 1) stackDeep: Int): Config {
            mStackDeep = stackDeep
            return this
        }

        fun setStackOffset(@IntRange(from = 0) stackOffset: Int): Config {
            mStackOffset = stackOffset
            return this
        }

        fun setWriteLogForRelease(isLog2FileForRelease: Boolean): Config {
            if (isLog2FileForRelease) {
                val pm = UtilsApplication.mContext.packageManager
                var name: String? = null
                try {
                    name = pm.getApplicationLabel(
                            pm.getApplicationInfo(mContext.packageName,
                                    PackageManager.GET_META_DATA)).toString()
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

                val logName = "LogInfo"
                var logFile = File(Environment.getExternalStorageDirectory().path + "/${logName}/")

                if (logFile.exists()) {
                    if (!StringUtils.isTrimEmpty(name)) {
                        var logFileByLable = File(Environment.getExternalStorageDirectory().path + "/${logName}/${name}/")
                        if (!logFileByLable.exists()) {
                            if (logFileByLable.mkdirs()) {
                                setDir(logFileByLable)
                            }
                        } else {
                            setDir(logFileByLable)
                        }
                    } else {
                        setDir(logFile)
                    }
                    setLog2FileSwitch(true)
                    setLogSwitch(true)
                }
            }
            return this
        }

        override fun toString(): String {
            return ("switch: " + mLogSwitch
                    + LINE_SEP + "console: " + mLog2ConsoleSwitch
                    + LINE_SEP + "tag: " + (if (mTagIsSpace) "null" else mGlobalTag)
                    + LINE_SEP + "head: " + mLogHeadSwitch
                    + LINE_SEP + "file: " + mLog2FileSwitch
                    + LINE_SEP + "dir: " + (if (mDir == null) mDefaultDir else mDir)
                    + LINE_SEP + "filePrefix: " + mFilePrefix
                    + LINE_SEP + "border: " + mLogBorderSwitch
                    + LINE_SEP + "singleTag: " + mSingleTagSwitch
                    + LINE_SEP + "consoleFilter: " + T[mConsoleFilter - V]
                    + LINE_SEP + "fileFilter: " + T[mFileFilter - V]
                    + LINE_SEP + "stackDeep: " + mStackDeep
                    + LINE_SEP + "mStackOffset: " + mStackOffset)
        }
    }

    private class TagHead internal constructor(internal var tag: String?, internal var consoleHead: Array<String?>?, internal var fileHead: String?)

    companion object {

        const val V = Log.VERBOSE
        const val D = Log.DEBUG
        const val I = Log.INFO
        const val W = Log.WARN
        const val E = Log.ERROR
        const val A = Log.ASSERT

        private val T = charArrayOf('V', 'D', 'I', 'W', 'E', 'A')

        private val FILE = 0x10
        private val JSON = 0x20
        private val XML = 0x30

        private val FILE_SEP = System.getProperty("file.separator")
        private val LINE_SEP = System.getProperty("line.separator")
        private val TOP_CORNER = "┌"
        private val MIDDLE_CORNER = "├"
        private val LEFT_BORDER = "│ "
        private val BOTTOM_CORNER = "└"
        private val SIDE_DIVIDER = "────────────────────────────────────────────────────────"
        private val MIDDLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
        private val TOP_BORDER = TOP_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
        private val MIDDLE_BORDER = MIDDLE_CORNER + MIDDLE_DIVIDER + MIDDLE_DIVIDER
        private val BOTTOM_BORDER = BOTTOM_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
        private val MAX_LEN = 3000
        @SuppressLint("SimpleDateFormat")
        private val FORMAT = SimpleDateFormat("MM-dd HH:mm:ss.SSS ")
        private val NOTHING = "log nothing"
        private val NULL = "null"
        private val ARGS = "args"
        private val PLACEHOLDER = " "
        val config = Config()
        private var sExecutor: ExecutorService? = null

        fun v(vararg contents: Any) {
            log(V, config.mGlobalTag, *contents)
        }

        fun vTag(tag: String, vararg contents: Any) {
            log(V, tag, *contents)
        }

        fun d(vararg contents: Any) {
            log(D, config.mGlobalTag, *contents)
        }

        fun dTag(tag: String, vararg contents: Any) {
            log(D, tag, *contents)
        }

        fun i(vararg contents: Any) {
            log(I, config.mGlobalTag, *contents)
        }

        fun iTag(tag: String, vararg contents: Any) {
            log(I, tag, *contents)
        }

        fun w(vararg contents: Any) {
            log(W, config.mGlobalTag, *contents)
        }

        fun wTag(tag: String, vararg contents: Any) {
            log(W, tag, *contents)
        }

        fun e(vararg contents: Any) {
            log(E, config.mGlobalTag, *contents)
        }

        fun eTag(tag: String, vararg contents: Any) {
            log(E, tag, *contents)
        }

        fun a(vararg contents: Any) {
            log(A, config.mGlobalTag, *contents)
        }

        fun aTag(tag: String, vararg contents: Any) {
            log(A, tag, *contents)
        }

        fun file(content: Any) {
            log(FILE or D, config.mGlobalTag, content)
        }

        fun file(@TYPE type: Int, content: Any) {
            log(FILE or type, config.mGlobalTag, content)
        }

        fun file(tag: String, content: Any) {
            log(FILE or D, tag, content)
        }

        fun file(@TYPE type: Int, tag: String, content: Any) {
            log(FILE or type, tag, content)
        }

        fun json(content: String) {
            log(JSON or D, config.mGlobalTag, content)
        }

        fun json(@TYPE type: Int, content: String) {
            log(JSON or type, config.mGlobalTag, content)
        }

        fun json(tag: String, content: String) {
            log(JSON or D, tag, content)
        }

        fun json(@TYPE type: Int, tag: String, content: String) {
            log(JSON or type, tag, content)
        }

        fun xml(content: String) {
            log(XML or D, config.mGlobalTag, content)
        }

        fun xml(@TYPE type: Int, content: String) {
            log(XML or type, config.mGlobalTag, content)
        }

        fun xml(tag: String, content: String) {
            log(XML or D, tag, content)
        }

        fun xml(@TYPE type: Int, tag: String, content: String) {
            log(XML or type, tag, content)
        }

        fun log(type: Int, tag: String?, vararg contents: Any) {
            if (!config.mLogSwitch || !config.mLog2ConsoleSwitch && !config.mLog2FileSwitch) return
            val type_low = type and 0x0f
            val type_high = type and 0xf0
            if (type_low < config.mConsoleFilter && type_low < config.mFileFilter) return
            val tagHead = processTagAndHead(tag)
            val body = processBody(type_high, *contents)
            if (config.mLog2ConsoleSwitch && type_low >= config.mConsoleFilter && type_high != FILE) {
                print2Console(type_low, tagHead.tag, tagHead.consoleHead, body)
            }
            if ((config.mLog2FileSwitch || type_high == FILE) && type_low >= config.mFileFilter) {
                print2File(type_low, tagHead.tag, tagHead.fileHead + body)
            }
        }

        private fun processTagAndHead(tag: String?): TagHead {
            var tag = tag
            if (!config.mTagIsSpace && !config.mLogHeadSwitch) {
                tag = config.mGlobalTag
            } else {
                val stackTrace = Throwable().stackTrace
                val stackIndex = 3 + config.mStackOffset
                if (stackIndex >= stackTrace.size) {
                    val targetElement = stackTrace[3]
                    val fileName = getFileName(targetElement)
                    if (config.mTagIsSpace && isSpace(tag)) {
                        val index = fileName.indexOf('.')// Use proguard may not find '.'.
                        tag = if (index == -1) fileName else fileName.substring(0, index)
                    }
                    return TagHead(tag, null, ": ")
                }
                var targetElement = stackTrace[stackIndex]
                val fileName = getFileName(targetElement)
                if (config.mTagIsSpace && isSpace(tag)) {
                    val index = fileName.indexOf('.')// Use proguard may not find '.'.
                    tag = if (index == -1) fileName else fileName.substring(0, index)
                }
                if (config.mLogHeadSwitch) {
                    val tName = Thread.currentThread().name
                    val head = Formatter()
                            .format("%s, %s.%s(%s:%d)",
                                    tName,
                                    targetElement.className,
                                    targetElement.methodName,
                                    fileName,
                                    targetElement.lineNumber)
                            .toString()
                    val fileHead = " [$head]: "
                    if (config.mStackDeep <= 1) {
                        return TagHead(tag, arrayOf(head), fileHead)
                    } else {
                        val consoleHead = arrayOfNulls<String>(Math.min(
                                config.mStackDeep,
                                stackTrace.size - stackIndex
                        ))
                        consoleHead[0] = head
                        val spaceLen = tName.length + 2
                        val space = Formatter().format("%" + spaceLen + "s", "").toString()
                        var i = 1
                        val len = consoleHead.size
                        while (i < len) {
                            targetElement = stackTrace[i + stackIndex]
                            consoleHead[i] = Formatter()
                                    .format("%s%s.%s(%s:%d)",
                                            space,
                                            targetElement.className,
                                            targetElement.methodName,
                                            getFileName(targetElement),
                                            targetElement.lineNumber)
                                    .toString()
                            ++i
                        }
                        return TagHead(tag, consoleHead, fileHead)
                    }
                }
            }
            return TagHead(tag, null, ": ")
        }

        private fun getFileName(targetElement: StackTraceElement): String {
            val fileName = targetElement.fileName
            if (fileName != null) return fileName
            // If name of file is null, should add
            // "-keepattributes SourceFile,LineNumberTable" in proguard file.
            var className = targetElement.className
            val classNameInfo = className.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (classNameInfo.size > 0) {
                className = classNameInfo[classNameInfo.size - 1]
            }
            val index = className.indexOf('$')
            if (index != -1) {
                className = className.substring(0, index)
            }
            return "$className.java"
        }

        private fun processBody(type: Int, vararg contents: Any): String {
            var body = NULL
            if (contents != null) {
                if (contents.size == 1) {
                    val `object` = contents[0]
                    if (`object` != null) body = `object`.toString()
                    if (type == JSON) {
                        body = formatJson(body)
                    } else if (type == XML) {
                        body = formatXml(body)
                    }
                } else {
                    val sb = StringBuilder()
                    var i = 0
                    val len = contents.size
                    while (i < len) {
                        val content = contents[i]
                        sb.append(ARGS)
                                .append("[")
                                .append(i)
                                .append("]")
                                .append(" = ")
                                .append(content.toString() ?: NULL)
                                .append(LINE_SEP)
                        ++i
                    }
                    body = sb.toString()
                }
            }
            return if (body.length == 0) NOTHING else body
        }

        private fun formatJson(json: String): String {
            var json = json
            try {
                if (json.startsWith("{")) {
                    json = JSONObject(json).toString(4)
                } else if (json.startsWith("[")) {
                    json = JSONArray(json).toString(4)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return json
        }

        private fun formatXml(xml: String): String {
            var xml = xml
            try {
                val xmlInput = StreamSource(StringReader(xml))
                val xmlOutput = StreamResult(StringWriter())
                val transformer = TransformerFactory.newInstance().newTransformer()
                transformer.setOutputProperty(OutputKeys.INDENT, "yes")
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
                transformer.transform(xmlInput, xmlOutput)
                xml = xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">$LINE_SEP")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return xml
        }

        private fun print2Console(type: Int,
                                  tag: String?,
                                  head: Array<String?>?,
                                  msg: String) {
            if (config.mSingleTagSwitch) {
                val sb = StringBuilder()
                sb.append(PLACEHOLDER).append(LINE_SEP)
                if (config.mLogBorderSwitch) {
                    sb.append(TOP_BORDER).append(LINE_SEP)
                    if (head != null) {
                        for (aHead in head) {
                            sb.append(LEFT_BORDER).append(aHead).append(LINE_SEP)
                        }
                        sb.append(MIDDLE_BORDER).append(LINE_SEP)
                    }
                    for (line in msg.split(LINE_SEP.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                        sb.append(LEFT_BORDER).append(line).append(LINE_SEP)
                    }
                    sb.append(BOTTOM_BORDER)
                } else {
                    if (head != null) {
                        for (aHead in head) {
                            sb.append(aHead).append(LINE_SEP)
                        }
                    }
                    sb.append(msg)
                }
                printMsgSingleTag(type, tag, sb.toString())
            } else {
                printBorder(type, tag, true)
                printHead(type, tag, head)
                printMsg(type, tag, msg)
                printBorder(type, tag, false)
            }
        }

        private fun printBorder(type: Int, tag: String?, isTop: Boolean) {
            if (config.mLogBorderSwitch) {
                Log.println(type, tag, if (isTop) TOP_BORDER else BOTTOM_BORDER)
            }
        }

        private fun printHead(type: Int, tag: String?, head: Array<String?>?) {
            if (head != null) {
                for (aHead in head) {
                    Log.println(type, tag, if (config.mLogBorderSwitch) LEFT_BORDER + aHead else aHead)
                }
                if (config.mLogBorderSwitch) Log.println(type, tag, MIDDLE_BORDER)
            }
        }

        private fun printMsg(type: Int, tag: String?, msg: String) {
            val len = msg.length
            val countOfSub = len / MAX_LEN
            if (countOfSub > 0) {
                var index = 0
                for (i in 0 until countOfSub) {
                    printSubMsg(type, tag, msg.substring(index, index + MAX_LEN))
                    index += MAX_LEN
                }
                if (index != len) {
                    printSubMsg(type, tag, msg.substring(index, len))
                }
            } else {
                printSubMsg(type, tag, msg)
            }
        }

        private fun printMsgSingleTag(type: Int, tag: String?, msg: String) {
            val len = msg.length
            val countOfSub = len / MAX_LEN
            if (countOfSub > 0) {
                if (config.mLogBorderSwitch) {
                    Log.println(type, tag, msg.substring(0, MAX_LEN) + LINE_SEP + BOTTOM_BORDER)
                    var index = MAX_LEN
                    for (i in 1 until countOfSub) {
                        Log.println(type, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP
                                + LEFT_BORDER + msg.substring(index, index + MAX_LEN)
                                + LINE_SEP + BOTTOM_BORDER)
                        index += MAX_LEN
                    }
                    if (index != len) {
                        Log.println(type, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP
                                + LEFT_BORDER + msg.substring(index, len))
                    }
                } else {
                    var index = 0
                    for (i in 0 until countOfSub) {
                        Log.println(type, tag, msg.substring(index, index + MAX_LEN))
                        index += MAX_LEN
                    }
                    if (index != len) {
                        Log.println(type, tag, msg.substring(index, len))
                    }
                }
            } else {
                Log.println(type, tag, msg)
            }
        }

        private fun printSubMsg(type: Int, tag: String?, msg: String) {
            if (!config.mLogBorderSwitch) {
                Log.println(type, tag, msg)
                return
            }
            val sb = StringBuilder()
            val lines = msg.split(LINE_SEP.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (line in lines) {
                Log.println(type, tag, LEFT_BORDER + line)
            }
        }

        private fun printSubMsg1(type: Int, tag: String, msg: String) {
            if (!config.mLogBorderSwitch) {

                return
            }
            val sb = StringBuilder()
            val lines = msg.split(LINE_SEP.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (line in lines) {
                Log.println(type, tag, LEFT_BORDER + line)
            }
        }

        private fun print2File(type: Int, tag: String?, msg: String) {
            val now = Date(System.currentTimeMillis())
            val format = FORMAT.format(now)
            val date = format.substring(0, 5)
            val time = format.substring(6)
            val fullPath = ((if (config.mDir == null) config.mDefaultDir else config.mDir)
                    + config.mFilePrefix + "-" + date + ".txt")
            if (!createOrExistsFile(fullPath)) {
                Log.e("LogUtils", "create $fullPath failed!")
                return
            }
            val sb = StringBuilder()
            sb.append(time)
                    .append(T[type - V])
                    .append("/")
                    .append(tag)
                    .append(msg)
                    .append(LINE_SEP)
            val content = sb.toString()
            input2File(content, fullPath)
        }

        private fun createOrExistsFile(filePath: String): Boolean {
            val file = File(filePath)
            if (file.exists()) return file.isFile
            if (!createOrExistsDir(file.parentFile)) return false
            try {
                val isCreate = file.createNewFile()
                if (isCreate) printDeviceInfo(filePath)
                return isCreate
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

        }

        private fun printDeviceInfo(filePath: String) {
            var versionName = ""
            var versionCode = 0
            try {
                val pi = Utils.getApp()
                        .packageManager
                        .getPackageInfo(Utils.getApp().packageName, 0)
                if (pi != null) {
                    versionName = pi.versionName
                    versionCode = pi.versionCode
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            val time = filePath.substring(filePath.length - 9, filePath.length - 4)
            val head = "************* Log Head ****************" +
                    "\nDate of Log        : " + time +
                    "\nDevice Manufacturer: " + Build.MANUFACTURER +
                    "\nDevice Model       : " + Build.MODEL +
                    "\nAndroid Version    : " + Build.VERSION.RELEASE +
                    "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                    "\nApp VersionName    : " + versionName +
                    "\nApp VersionCode    : " + versionCode +
                    "\n************* Log Head ****************\n\n"
            input2File(head, filePath)
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

        private fun input2File(input: String, filePath: String) {
            if (sExecutor == null) {
                sExecutor = Executors.newSingleThreadExecutor()
            }
            val submit = sExecutor!!.submit(Callable {
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

            Log.e("LogUtils", "log to $filePath failed!")
        }
    }
}
