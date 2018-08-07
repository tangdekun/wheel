package utils

import java.util.concurrent.*

object ThreadPoolExecutorUtil {
    /**
     * 核心工作的线程个数
     * */
    var mCoreSize = 5
        private set
    /**
     * 工作的最大线程个数
     * */
    var mMaxSize = 10
        private set
    /**
     * 额外线程空闲的时间
     * */
    var keepAliveTime = 3000L
        private set
    /**
     * 额外线程空闲的时间对应的单位
     * */
    var mTimeUnit = TimeUnit.MILLISECONDS
        private set
    /**
     *
     * 任务队列
     * */
    var workQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>(16)
        private set


    var mExecutor: ThreadPoolExecutor? = null
    /**
     * 线程工厂,如何去创建线程
     * */
    var mThreadFactory: ThreadFactory = Executors.defaultThreadFactory()
        private set
    /**
     * 任务队列添加异常的捕捉器,异常处理策略
     * */
    var mRejectedExecutionHandler = ThreadPoolExecutor.AbortPolicy()
        private set

    fun setCoreSize(mCoreSize: Int): ThreadPoolExecutorUtil {
        this.mCoreSize = mCoreSize
        return this
    }

    fun setWorkQueue(workQueue: BlockingQueue<Runnable>): ThreadPoolExecutorUtil {
        this.workQueue = workQueue
        return this
    }

    fun setMaxSize(mMaxSize: Int): ThreadPoolExecutorUtil {
        this.mMaxSize = mMaxSize
        return this
    }

    fun setKeepAliveTimeAndTimeUnit(keepAliveTime: Long, mTimeUnit: TimeUnit): ThreadPoolExecutorUtil {
        this.keepAliveTime = keepAliveTime
        this.mTimeUnit = mTimeUnit
        return this
    }

    fun build(): ThreadPoolExecutor {
        if (mExecutor == null || mExecutor!!.isShutdown) {
            mExecutor = ThreadPoolExecutor(mCoreSize, mMaxSize, keepAliveTime, mTimeUnit, workQueue, mThreadFactory, mRejectedExecutionHandler)
        }
        return mExecutor as ThreadPoolExecutor
    }

    fun exec(task: Runnable) {
        if (task == null) {
            return
        }
        build()
        mExecutor!!.execute(task)
    }
//    fun submit(task: Runnable):Future<Any> =
//        if (task != null) {
//            cheakPool()
//         mExecutor!!.submit(task)
//        }
//        else{
//           null
//        }
//

    private fun cheakPool() {
        if (mExecutor == null || mExecutor!!.isShutdown) {
            build()
        }
    }

}