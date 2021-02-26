@file:Suppress("unused", "SpellCheckingInspection")

object Android {
    const val compileSdkVersion = 30
    const val buildToolsVersion = "30.0.3"
    const val minSdkVersion = 21
    const val targetSdkVersion = 30

    const val versionName = "1.0"
    const val versionCode = 1
}

object Versions {
    const val kotlin = "1.4.30"

    const val corektx = "1.3.2"
    const val annotation = "1.1.0"
    const val appcompat = "1.2.0"
    const val fragmentktx = "1.3.0"
    const val navigation = "2.3.3"
    const val lifecycle = "2.3.0"
    const val lifecycle_extensions = "2.2.0"
    const val viewpager2 = "1.0.0"
    const val constraintlayout = "2.0.4"

    const val rxjava = "3.0.10"
    const val rxandroid = "3.0.0"
    const val retrofit2 = "2.9.0"
    const val okhttp3 = "4.9.0"
    const val okio = "2.8.0"
    const val gson = "2.8.6"
    const val disklrucache = "2.0.2"

    const val glide = "4.12.0"
    const val dagger2 = "2.33"
    const val utilcode = "1.30.6"
    const val leakcanary = "2.6"
    const val unpeeklivedata = "4.5.0-beta1"

    const val rxerrorhandler = "3.0.3"
    const val loadsir = "2.0.6"
}

object Publish {
    const val userOrg = "weixia" //bintray.com用户名
    const val groupId = "me.tiamosu" //jcenter上的路径
    const val publishVersion = "2.0.8" //版本号
    const val desc = "Oh hi, this is a nice description for a project, right?"
    const val website = "https://github.com/tiamosu/X-MVVMFly"
    const val gitUrl = "https://github.com/tiamosu/X-MVVMFly.git"
    const val email = "djy2009wenbi@gmail.com"
}

object Deps {
    //androidx
    const val androidx_core_ktx = "androidx.core:core-ktx:${Versions.corektx}"
    const val androidx_annotation = "androidx.annotation:annotation:${Versions.annotation}"
    const val androidx_appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val androidx_viewpager2 = "androidx.viewpager2:viewpager2:${Versions.viewpager2}"
    const val androidx_constraint_layout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"

    //navigation
    const val androidx_fragment = "androidx.fragment:fragment:${Versions.fragmentktx}"
    const val androidx_navigation_runtime =
        "androidx.navigation:navigation-runtime:${Versions.navigation}"

    //lifecycle
    const val lifecycle_runtime = "androidx.lifecycle:lifecycle-runtime:${Versions.lifecycle}"
    const val lifecycle_common_java8 =
        "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
    const val lifecycle_extensions =
        "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle_extensions}"
    const val lifecycle_viewmodel_ktx =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val lifecycle_livedata_ktx =
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"

    //kotlin
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val kotlin_stdlib_jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"

    //dagger2：依赖注入框架 https://github.com/google/dagger
    const val dagger2 = "com.google.dagger:dagger:${Versions.dagger2}"
    const val dagger2_apt = "com.google.dagger:dagger-compiler:${Versions.dagger2}"

    //leakcanary：内存泄漏检测 https://github.com/square/leakcanary
    const val leakcanary_android =
        "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"

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
    const val rxerrorhandler = "me.tiamosu:x-rxerrorhandler:${Versions.rxerrorhandler}"

    //utilcode：常用工具类库 https://github.com/Blankj/AndroidUtilCode
    const val utilcode = "com.blankj:utilcodex:${Versions.utilcode}"

    //disklrucache：硬盘缓存管理工具 https://github.com/JakeWharton/DiskLruCache
    const val disklrucache = "com.jakewharton:disklrucache:${Versions.disklrucache}"

    //okhttp：高效的http客户端 https://github.com/square/okhttp
    const val okhttp3 = "com.squareup.okhttp3:okhttp:${Versions.okhttp3}"
    const val okhttp3_logging_interceptor =
        "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp3}"

    //搭配okhttp使用 https://github.com/square/okio
    const val okio = "com.squareup.okio:okio:${Versions.okio}"

    //glide：强大的图片加载框架 https://github.com/bumptech/glide
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glide_compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    //loadSir：加载反馈页管理框架 https://github.com/tiamosu/LoadSir
    const val loadsir = "me.tiamosu:loadsir:${Versions.loadsir}"

    //unPeekLiveData：解决LiveData数据倒灌 https://github.com/KunMinX/UnPeek-LiveData
    const val unpeek_livedata = "com.kunminx.archi:unpeek-livedata:${Versions.unpeeklivedata}"

    //gson：Google 提供的用来在 Java 对象和 JSON 数据之间进行映射的 Java 类库 https://github.com/google/gson
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
}