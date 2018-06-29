package com.m4399.pluginapk.corelibrary.internal

import android.app.Instrumentation
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageStats
import android.util.Log
import dalvik.system.DexClassLoader

import java.io.File
import java.lang.reflect.Method

class LoadedPlugin {
    private val TAG: String = "LoadedPlugin"

    private var host: Context
    //private var mPackage: Package
    private val applicationInfo: ApplicationInfo
    private var mPackInfo: PackageInfo
    private var mClassLoader: DexClassLoader
    //private var mInstrumentationinfos: Map<ComponentName, Instrumentation>


    constructor(host: Context, apk: File) {
        this.host = host
        getPackageParser(apk)
        val pm: PackageManager = host.packageManager
        mPackInfo = pm.getPackageArchiveInfo(apk.path, 0)


        val dexOutDirPath = host.filesDir.path + File.separator + "dexOutDir"
        val dexOutDir = File(dexOutDirPath)
        if (!dexOutDir.exists()) {
            dexOutDir.mkdirs()
        }

        mClassLoader = createClassLoader(host, apk, dexOutDir, null)
        applicationInfo = generateApplicationInfo(getPackageParser(apk), 0, getPackageUserState())
        Log.d("dsff", "sdfsaf")
    }

    private fun hookPackageParser(): Class<*> {
        return  Class.forName("android.content.pm.PackageParser")
    }

    public fun getPackageName(): String {
        return mPackInfo.packageName
    }

    private fun getPackageParser(apk: File): Any {
        val packageParserClass: Class<*> = Class.forName("android.content.pm.PackageParser")
        val packageParser: Any = packageParserClass.newInstance()
        val parserPackMethod = packageParserClass.getDeclaredMethod("parsePackage", File::class.java, Int::class.java)
        parserPackMethod.isAccessible = true
        return parserPackMethod.invoke(packageParser, apk, 0)
    }

    private fun getPackageUserState(): Any {
        val packageUserStateClass: Class<*> = Class.forName("android.content.pm.PackageUserState")
        return  packageUserStateClass.newInstance()
    }

    private fun generateApplicationInfo( p: Any, flag: Int, state: Any): ApplicationInfo {
        val packageParserClass: Class<*> = Class.forName("android.content.pm.PackageParser")
        val packageParser: Any = packageParserClass.newInstance()
        val packageUserStateClass: Class<*> = Class.forName("android.content.pm.PackageUserState")
        val packageClass: Class<*> = Class.forName("android.content.pm.PackageParser\$Package")

        val generateApplicationMethod: Method = packageParserClass.getDeclaredMethod("generateApplicationInfo", packageClass, Int::class.java, packageUserStateClass)
        return generateApplicationMethod.invoke(packageParser, p, flag, state) as ApplicationInfo
    }

    companion object {
        private fun createClassLoader(context: Context, apk:File, dexOutFile: File, nativeLibDir: File?): DexClassLoader {
            var nativeLibDirPath: String? = null
            if (nativeLibDir != null) {
                nativeLibDirPath = nativeLibDir.path
            }

            return  DexClassLoader(apk.path, dexOutFile.path, nativeLibDirPath, context.applicationContext.classLoader)
        }
    }

}