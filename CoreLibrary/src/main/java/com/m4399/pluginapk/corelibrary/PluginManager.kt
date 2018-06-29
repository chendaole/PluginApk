package com.m4399.pluginapk.corelibrary

import android.content.Context
import android.os.Environment
import dalvik.system.DexClassLoader
import java.io.File
import java.io.FileNotFoundException

import com.m4399.pluginapk.corelibrary.internal.LoadedPlugin
import java.util.concurrent.ConcurrentHashMap

class PluginManager {
    companion object {
        private var instance: PluginManager? = null

        @Synchronized
        fun getInstance(context: Context?): PluginManager {
            if (instance == null) {
                if (context == null) {
                    throw IllegalArgumentException("context is null")
                }
                instance =  PluginManager(context)
            }

            return instance!!
        }
    }


    private var context: Context
    private lateinit var pluginRootPath: String
    private val mPlugins:Map<String, LoadedPlugin> = ConcurrentHashMap()

    constructor(context: Context) {
        this.context  = context
    }

    public fun init(): Boolean {
        val isExist = Environment.getExternalStorageState() == android.os.Environment.MEDIA_MOUNTED

        if (!isExist) {
            return false
        }

        try {
            pluginRootPath = Environment.getExternalStorageDirectory().path + File.separator + "4399PluginApk"
            val pluginRootDir = File(pluginRootPath)

            if (!pluginRootDir.exists()) {
                pluginRootDir.mkdirs()
            }

        } catch (e: Exception) {
            return  false
        }

        return  true
    }

    public fun loaderPlugin(pkg: String): Boolean {
        val apkPath = pluginRootPath + File.separator + pkg + ".apk"
        val apkFile: File =  File(apkPath)
        if (!apkFile.exists()) {
            throw FileNotFoundException("apk not found")
        }

        try {
            val loadPlugin = LoadedPlugin(context, apkFile)
            mPlugins.plus(Pair(loadPlugin.getPackageName(), loadPlugin))
        } catch (e:Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

}