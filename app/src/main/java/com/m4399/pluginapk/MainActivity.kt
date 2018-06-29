package com.m4399.pluginapk

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build

import com.m4399.pluginapk.corelibrary.PluginManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)

        if (!hasPermission()) {
            requestPermission()
        }

        PluginManager.getInstance(newBase)!!.init()
    }

    override fun onResume() {
        super.onResume()

        //界面加载完成, 绑定事件
        initEvent()
    }

    private fun initEvent() {
        val launchPluginBtn = findViewById<Button>(R.id.btn_launch_plugin)
        launchPluginBtn.setOnClickListener{
            PluginManager.getInstance(this).loaderPlugin("com.m4399.pluginapk.demo")
        }

    }

    private fun loadPlugin() {

    }

    private  fun hasPermission(): Boolean {
        //API 版本大于 23 时，启用动态请求权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
        return true
    }

    private fun requestPermission() {
        //TODO:请求读写外部存储卡权限
    }

}
