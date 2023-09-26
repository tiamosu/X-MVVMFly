@file:Suppress("unused", "SpellCheckingInspection")

object Android {
    const val compileSdk = 33
    const val minSdk = 21
    const val targetSdk = 33

    const val versionName = "1.0"
    const val versionCode = 1
}

object Versions {
    const val kotlin = "1.8.20"

    const val rxjava = "3.1.7"
    const val rxandroid = "3.0.2"
    const val retrofit2 = "2.9.0"
    const val okhttp3 = "4.11.0"
    const val gson = "2.10.1"
    const val disklrucache = "2.0.2"

    const val glide = "4.16.0"
    const val dagger2 = "2.48"
    const val utilcode = "1.31.1"
    const val leakcanary = "2.12"
    const val unpeeklivedata = "7.8.0"

    const val fly_navigation = "1.7.1"
    const val rxerrorhandler = "4.0.2"
    const val loadsir = "3.0.1"
}

object Deps {
    //fly-navigation
    const val fly_navigation = "com.gitee.tiamosu:fly-navigation:${Versions.fly_navigation}"
    const val fly_databinding = "com.gitee.tiamosu:fly-databinding:${Versions.fly_navigation}"

    //androidx
    const val viewpager2 = "androidx.viewpager2:viewpager2:1.0.0"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.1.4"
    const val recyclerview = "androidx.recyclerview:recyclerview:1.3.1"

    //dagger2：依赖注入框架 https://github.com/google/dagger
    const val dagger2 = "com.google.dagger:dagger:${Versions.dagger2}"
    const val dagger2_apt = "com.google.dagger:dagger-compiler:${Versions.dagger2}"

    //leakcanary：内存泄漏检测 https://github.com/square/leakcanary
    const val leakcanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"

    //retrofit2：基于okhttp封装的网络请求框架 https://github.com/square/retrofit
    const val retrofit2 = "com.squareup.retrofit2:retrofit:${Versions.retrofit2}"
    const val retrofit2_adapter_rxjava2 =
        "com.squareup.retrofit2:adapter-rxjava3:${Versions.retrofit2}"
    const val retrofit2_converter_gson =
        "com.squareup.retrofit2:converter-gson:${Versions.retrofit2}"

    //rx：基于事件流、实现异步操作的库 https://github.com/ReactiveX/RxJava
    const val rxjava3 = "io.reactivex.rxjava3:rxjava:${Versions.rxjava}"
    const val rxandroid3 = "io.reactivex.rxjava3:rxandroid:${Versions.rxandroid}"

    //rxjava错误处理 https://github.com/tiamosu/X-RxErrorHandler
    const val rxerrorhandler = "com.gitee.tiamosu:RxErrorHandler:${Versions.rxerrorhandler}"

    //utilcode：常用工具类库 https://github.com/Blankj/AndroidUtilCode
    const val utilcode = "com.blankj:utilcodex:${Versions.utilcode}"

    //disklrucache：硬盘缓存管理工具 https://github.com/JakeWharton/DiskLruCache
    const val disklrucache = "com.jakewharton:disklrucache:${Versions.disklrucache}"

    //okhttp：高效的http客户端 https://github.com/square/okhttp
    const val okhttp3 = "com.squareup.okhttp3:okhttp:${Versions.okhttp3}"
    const val okhttp3_logging_interceptor =
        "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp3}"

    //glide：强大的图片加载框架 https://github.com/bumptech/glide
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glide_compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    //loadSir：加载反馈页管理框架 https://github.com/tiamosu/LoadSir
    const val loadsir = "com.gitee.tiamosu:loadsir:${Versions.loadsir}"

    //unPeekLiveData：解决LiveData数据倒灌 https://github.com/KunMinX/UnPeek-LiveData
    const val unpeek_livedata = "com.kunminx.arch:unpeek-livedata:${Versions.unpeeklivedata}"

    //gson：Google 提供的用来在 Java 对象和 JSON 数据之间进行映射的 Java 类库 https://github.com/google/gson
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
}