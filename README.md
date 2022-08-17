# X-MVVMFly


## Jetpack-Fly
* [传送门](https://github.com/tiamosu/Jetpack-Fly)

## Wiki

[详细使用方法及扩展功能，请参照 Wiki (开发前必看!!!)](https://github.com/tiamosu/X-MVVMFly/wiki)

## Requirements

适用于 Android 5.0 + (21 + API级别) 和 Java 11 +。

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
implementation 'com.gitee.tiamosu:fly:4.5.1'
```

### fly-http（可选）

```groovy
implementation 'com.gitee.tiamosu:fly-http:4.5.1'
```

### fly-imageloader-glide（可选）

```groovy
implementation 'com.gitee.tiamosu:fly-imageloader-glide:4.5.1'
```

### fly模块

#### 依赖库

* [FlyNavigation](https://gitee.com/tiamosu/FlyNavigation)
* [RxJava](https://github.com/ReactiveX/RxJava)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [RxErrorHandler](https://gitee.com/tiamosu/X-RxErrorHandler)
* [Retrofit](https://github.com/square/retrofit)
* [Gson](https://github.com/google/gson)
* [Okhttp](https://github.com/square/okhttp)
* [Dagger](https://github.com/google/dagger)
* [AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)
* [UnPeek-LiveData](https://github.com/KunMinX/UnPeek-LiveData)

### fly-http模块

#### 依赖库

* [DiskLruCache](https://github.com/JakeWharton/DiskLruCache)
* [Okhttp](https://github.com/square/okhttp)

### fly-http模块

#### 依赖库

* [Glide](https://github.com/bumptech/glide)

## [UpdateLog](https://github.com/tiamosu/X-MVVMFly/blob/master/CHANGELOG.md)

## *特别感谢*

* [MVPArms](https://github.com/JessYanCoding/MVPArms)
* [RxEasyHttp](https://github.com/zhou-you/RxEasyHttp)
* [FlyNavigation](https://gitee.com/tiamosu/FlyNavigation)
* [GsonFactory](https://github.com/getActivity/GsonFactory)
* [AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)
* [AndroidProject](https://github.com/getActivity/AndroidProject)
* [Jetpack-MVVM-Best-Practice](https://github.com/KunMinX/Jetpack-MVVM-Best-Practice)
