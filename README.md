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
        jcenter()
        maven { url 'https://jitpack.io' }

        //或者 sonatype
        maven {url "https://s01.oss.sonatype.org/content/groups/public"}
        maven {url "https://s01.oss.sonatype.org/content/repositories/releases"}
    }
}
```

### fly（必需）
```groovy
implementation 'com.gitee.tiamosu:fly:4.3.1'
```

### fly-http（可选）
```groovy
implementation 'com.gitee.tiamosu:fly-http:4.3.1'
```

### fly-imageloader-glide（可选）
```groovy
implementation 'com.gitee.tiamosu:fly-imageloader-glide:4.3.1'
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
