# X-MVVMFly

## Wiki

[详细使用方法及扩展功能，请参照 Wiki (开发前必看!!!)](https://github.com/tiamosu/X-MVVMFly/wiki)

## Requirements

适用于 Android 5.0 + (21 + API级别) 和 Java 8 +。

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }

        //或者 sonatype
        maven { url "https://s01.oss.sonatype.org/content/groups/public" }
        maven { url "https://s01.oss.sonatype.org/content/repositories/releases" }
    }
}
```

### fly（必需）

```groovy
implementation 'com.gitee.tiamosu:fly:4.3.4'
```

### fly-http（可选）

```groovy
implementation 'com.gitee.tiamosu:fly-http:4.3.4'
```

### fly-imageloader-glide（可选）

```groovy
implementation 'com.gitee.tiamosu:fly-imageloader-glide:4.3.4'
```

### fly模块

#### 依赖库

```gradle
com.gitee.tiamosu:fly-navigation:1.5.7
com.gitee.tiamosu:fly-databinding:1.5.7
io.reactivex.rxjava3:rxjava:3.1.3
io.reactivex.rxjava3:rxandroid:3.0.0
com.gitee.tiamosu:RxErrorHandler:4.0.1
com.squareup.retrofit2:retrofit:2.9.0
com.squareup.retrofit2:adapter-rxjava3:2.9.0
com.squareup.retrofit2:converter-gson:2.9.0
com.google.code.gson:gson:2.8.6
com.squareup.okhttp3:okhttp:4.9.3
com.google.dagger:dagger:2.40.5
com.blankj:utilcodex:1.31.0
com.kunminx.arch:unpeek-livedata:7.2.0-beta1
```

### fly-http模块

#### 依赖库

```gradle
com.jakewharton:disklrucache:2.0.2
com.squareup.okhttp3:logging-interceptor:4.9.3
```

### fly-http模块

#### 依赖库

```gradle
com.github.bumptech.glide:glide:4.12.0
```

## [UpdateLog](https://github.com/tiamosu/X-MVVMFly/blob/master/CHANGELOG.md)

## *特别感谢*

* [MVPArms](https://github.com/JessYanCoding/MVPArms)
* [RxEasyHttp](https://github.com/zhou-you/RxEasyHttp)
* [FlyNavigation](https://gitee.com/tiamosu/FlyNavigation)
* [GsonFactory](https://github.com/getActivity/GsonFactory)
* [AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)
* [AndroidProject](https://github.com/getActivity/AndroidProject)
* [Jetpack-MVVM-Best-Practice](https://github.com/KunMinX/Jetpack-MVVM-Best-Practice)
