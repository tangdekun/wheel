package com.hp.baseres

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.baseres_activity_main.*
import kotlinx.android.synthetic.main.baseres_activity_main.view.*
import utils.LogUtils

class MainActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.baseres_activity_main)
        PermissionUtils.permission("android.permission.WRITE_EXTERNAL_STORAGE").callback(object : PermissionUtils.SimpleCallback {
            override fun onGranted() {
                ToastUtils.showShort("允许打开读写权限")
//                CrashUtils.init()
            }

            override fun onDenied() {
                ToastUtils.showShort("权限被拒绝")

            }
        }).request()

        tv.text = "View 类资源文件${4 / 0}"
        LogUtils.d("View 类资源文件")
        LogUtils.d("测试多个文件会怎么生成")

    }


    override fun onClick(v: View?) {
        v?.let {
            when (v) {
                v.tv -> {
                    var intent = Intent(this, TestActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

}


