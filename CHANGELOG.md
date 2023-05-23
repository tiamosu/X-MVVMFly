# 新版本4.5.5

* rxjava = "3.1.6"
* rxandroid = "3.0.2"
* okhttp3 = "4.11.0"
* gson = "2.10.1"
* glide = "4.15.1"
* dagger2 = "2.46.1"

# 新版本4.5.4

* glide = "4.14.2"
* utilcode = "1.31.1"

# 新版本4.5.3

* glide = "4.14.1"
* dagger2 = "2.44"
* unpeeklivedata = "7.8.0"
* fly_navigation = "1.7.0"

# 新版本4.5.2

* 兼容 Gson 2.9.1 版本

# 新版本4.5.1

* dagger2 = "2.43.2"
* fly_navigation = "1.6.9"
* 删除 IFlyViewModel，全局 ViewModel 实现已通过 ApplicationInstance 替换

# 新版本4.5.0

* <uses-sdk tools:overrideLibrary="com.kunminx.unpeeklivedata"></uses-sdk>

# 新版本4.4.9

* 新增支持解析 Map 类型的对象

# 新版本4.4.8

* unpeeklivedata = "7.6.0"
* fly_navigation = "1.6.8"

# 新版本4.4.7

* kotlin = "1.7.0"
* okhttp3 = "4.10.0"

# 新版本4.4.6

* rxjava = "3.1.5"
* fly_navigation = "1.6.7"

# 新版本4.4.5

* gradle = 7.2.0
* glide = "4.13.2"
* dagger2 = "2.42"
* fly_navigation = "1.6.6

# 新版本4.4.4

* fly_navigation = "1.6.5"
*

FlyApplication调整，在使用全局ViewModel（SharedViewModel）共享时，可自行在Application中实现IFlyViewModel，不再强制继承FlyApplication

# 新版本4.4.3

* kotlin = "1.6.21"
* fly_navigation = "1.6.4"
* 网络监听优化

# 新版本4.4.2

* rxjava = "3.1.4"
* 网络安全配置修改
* 移除android:name="android.permission.MANAGE_EXTERNAL_STORAGE"默认权限配置
* ActivityAnimationStyle名称改为FlyActivityAnimationStyle

# 新版本4.4.1

* fly_navigation = "1.6.3"

# 新版本4.4.0

* network_security_config 配置修改

# 新版本4.3.9

* fly_navigation = "1.6.2"

# 新版本4.3.8

* glide = "4.13.1"
* fly_navigation = "1.6.0"
* loadsir = "3.0.0"

# 新版本4.3.7

* gson = "2.9.0"
* dagger2 = "2.41"
* fly_navigation = "1.5.9"

# 新版本4.3.6

* gson = "2.8.9"
* glide = "4.13.0"

# 新版本4.3.5

* fly_navigation = "1.5.8"

# 新版本4.3.4

* compileSdk = 32
* kotlin = "1.6.10"
* constraintlayout = "2.1.3"
* dagger2 = "2.40.5"
* fly_navigation = "1.5.7"

# 新版本4.3.3

* dagger2 = "2.40.4"
* utilcode = "1.31.0"
* rxerrorhandler = "4.0.1"

# 新版本4.3.2

* constraintlayout = "2.1.2"
* rxjava = "3.1.3"
* okhttp3 = "4.9.3"
* dagger2 = "2.40.2"
* fly_navigation = "1.5.6"

# 新版本4.3.1

* fly_navigation = "1.5.5"

# 新版本4.3.0

* gradle 7.0.3
* jdk 11 适配
* kotlin = "1.6.0"
* fly_navigation = "1.5.3"

# 新版本4.2.5

* dagger2 = "2.40"
* fly_navigation = "1.4.0"

# 新版本4.2.4

* Glide加载优化，新增GlideFlyExt扩展

# 新版本4.2.3

* Glide加载优化，支持生命周期绑定：View、Context、Fragment、Activity、FragmentActivity

# 新版本4.2.2

* rxjava = "3.1.2"
* 自定义Schedulers的线程池，在频繁使用Rxjava的时候仅使用单个调用度的实例

# 新版本4.2.1

* RxJava 回退3.1.1，修复问题：java.lang.OutOfMemoryError: pthread_create (1040KB stack) failed: Try again

# 新版本4.2.0

* rxjava = "3.1.2"

# 新版本4.1.9

* okhttp3 = "4.9.2"
* dagger2 = "2.39.1"

# 新版本4.1.8

* dagger2 = "2.39"
* fly_navigation = "1.2.5"

# 新版本4.1.6

* createObserver() 调用回退

# 新版本4.1.5

* constraintlayout = "2.1.1"
* fly_navigation = "1.2.3"
* 修复 LiveData 监听失效问题

# 新版本4.1.4

* rxjava = "3.1.1"

# 新版本4.1.3

* 修复 okhttp header 解析的错误问题：java.lang.IllegalArgumentException: Unexpected char

# 新版本4.1.2

* fly_navigation = "1.2.2"
* ImageLoader 图片加载，可自行传入context上下文

# 新版本4.1.1

* removeCallbacks 优化

# 新版本4.1.0

* fly_navigation = "1.2.1"，lazyDataBinding 优化

# 新版本4.0.9

* fly_navigation = "1.2.0"，lazyDataBinding 优化

# 新版本4.0.8

* 网络状态变化监听优化

# 新版本4.0.7

* rxjava = "3.1.0"
* 优先缓存加载报错修复
* 网络状态变化监听优化

# 新版本4.0.6

* dialog优化

# 新版本4.0.5

* 捕获网络监听注册产生的bug
* unpeeklivedata = "7.2.0-beta1"
* fly_navigation = "1.1.8"
* Loading加载优化
* 兼容了华为平板 5.1 5.0机型上，Register too many Broadcast Receivers 的问题

# 新版本4.0.4

* 页面真实可见性判断优化

# 新版本4.0.3

* fly_navigation = "1.1.7"

# 新版本4.0.2

* fly_navigation = "1.1.6"
* constraintlayout = "2.1.0"
* Loading弹框加载优化
* 修复layoutId为0时，不执行initView()、initEvent()、createObserver()函数

# 新版本4.0.1

* 点击外部隐藏软键盘优化

# 新版本4.0.0

* dagger2 = "2.38.1"
* unpeeklivedata = "6.1.0-beta1"
* fly_navigation = "1.1.5"
* 注意：initEvent() 函数位置调整至 initView() 之后

# 新版本3.0.9

* fly_navigation = "1.1.4"

# 新版本3.0.8

* 网络状态变化监听调试
* fly_navigation = "1.1.3"

# 新版本3.0.7

* recyclerview = "1.2.1"
* fly_navigation = "1.1.2"
* 添加deepClone深度克隆转化

# 新版本3.0.6

* 移除 Fragment 按键事件派发

# 新版本3.0.5

* unpeeklivedata = "6.0.0-beta1"
* fly_navigation = "1.1.1"

# 新版本3.0.4

* unPeekLiveData 升级
* dagger2 2.37
* Glide加载配置优化,transformation冲突问题

# 新版本3.0.3

* fly_navigation 1.1.0

# 新版本3.0.2

* fly_navigation 1.0.9

# 新版本3.0.1

* 依赖冲突处理

# 新版本3.0.0

* 接入 FlyNavigation
* ViewAction 移除 getLayoutId()、setContentView()、isNeedReload()
* BaseFlyActivity：initEvent() 及 doBusiness() 挪至 onResume() 进行懒加载
* BaseFlyFragment：initEvent() 挪至 onFlyLazyInitView() 进行懒加载
* 移除 NavigationExt 扩展，请使用 FlyNavigationExt 扩展
* lazyAppViewModel() 获取全局 ViewModel 调整

# 新版本2.2.9

* fragment 1.3.4
* appcompat 1.3.0
* dagger2 2.36
* corektx 1.5.0
* fragment回退优化，移除防抖

# 新版本2.2.8

* 添加单独配置是否打印数据请求结果

# 新版本2.2.7

* dagger2 2.35
* glide加载配置优化
* 新增Activity、Fragment专场动画
* createDir 创建文件夹优化
* getSystemService 函数调整
* 修复导航图中action不加动画报错问题。
* Fragment页面跳转内存泄漏优化

# 新版本2.2.6

* loading弹框加载优化

# 新版本2.2.5

* navigation 2.3.5
* dagger2 2.34.1
* loading弹框加载优化

# 新版本2.2.4

* rxjava 3.0.12
* dagger2 2.34
* fragment 1.3.2
* lifecycle 2.3.1

# 新版本2.2.3

* okhttp3 4.9.1

# 新版本2.2.2

* loading弹框展示隐藏添加同步注解
* fragmentktx 1.3.1
* navigation 2.3.4

# 新版本2.2.1

* library 重新发布至 JitPack

# 新版本2.1.7

* 移除Apache HTTP client

# 新版本2.1.6

* okHttp请求打印优化

# 新版本2.1.5

* FileUtils 提供获取Glide缓存目录文件以及Http数据缓存目录文件
* Glide加载图片设置高斯模糊优化

# 新版本2.1.4

* FlyHttp添加setPrintEnable函数，默认使用HttpLoggingInterceptor拦截器进行输出

# 新版本2.1.3

* GSON 解析容错优化

# 新版本2.1.2

* rxjava 3.0.11
* Loader加载loading弹框优化

# 新版本2.1.1

* loading弹框样式调整，支持继承FlyLoadingDialog传入主题样式

# 新版本2.1.0

* 添加 navigate 进行页面跳转异常捕获

# 新版本2.0.9

* Glide 加载配置 transform 支持传入多个参数

# 新版本2.0.8

* HandlerAction 优化

# 新版本2.0.7

* 优化loading弹框加载内存泄漏问题。

# 新版本2.0.6

* dagger 2.33
* 修复loading弹框隐藏无效问题。

# 新版本2.0.5

* Activity、Fragment添加Bundle、Handler、Keyboard相关操作。
* 添加默认loading弹框实现，支持自定义配置，通过showLoading(config)、hideLoading执行

# 新版本2.0.4

* 懒加载初始化优化，修复可能出现多次调用懒加载初始化函数问题。

# 新版本2.0.3

* kotlin 1.4.30
* utilcode 1.30.6
* fragment 懒加载初始化优化，防止切换动画未加载完毕时进行数据加载渲染卡顿
* 注意：fragment 加载函数周期顺序进行了调整，如下：

```
[onAttach] → [onCreate] → [initParameters] → [onCreateView] → [onViewCreated] → [initView] → 
[createObserver] → [onActivityCreated] → [onResume] → [initEvent] → [onFlySupportVisible] → 
[onFlyLazyInitView] → [doBusiness] → [onPause] → [onFlySupportInvisible] → [onDestroyView] → 
[onDestroy] → [onDetach]
```

# 新版本2.0.2

* 依赖调整

# 新版本2.0.1

* dagger 2.32
* fragment 1.3.0
* navigation 2.3.3
* loadsir 2.0.6
* rxerrorhandler 3.0.3
* 添加线程切换函数，launchMain，launchIO，launch（postOnMain、post 已弃用）
* 添加 NoCacheCustomCallback.kt 无缓存自定义回调，自行进行解析回调处理。

# 新版本2.0.0

* Fragment 可见性优化

# 新版本1.9.2

* Fragment 可见性优化，兼容 Action 设定 app:launchSingleTop="true"

# 新版本1.9.1

* Fragment 可见性优化，兼容 ViewPager

# 新版本1.9.0

* rxjava 3.0.10
* GSON 解析容错优化

# 新版本1.8.9

* GSON 解析容错优化，目前支持的类型有（JsonObject、JsonArray、String、Boolean、Int、Long、Float、Double、BigDecimal）

# 新版本1.8.8

* Glide 4.12.0
* 网络请求调试，onStart函数添加(disposable: Disposable)参数，可用于数据请求前判断是否有网络可用等。

# 新版本1.8.3

* GsonFactory 文件调整

# 新版本1.8.2

* 新增GsonFactory，Gson解析容错
* dagger2 2.31.2

# 新版本1.8.1

* 修复dialog bug

# 新版本1.8.0

* 重新发布

# 新版本1.7.8

* 优化navigation跳转
* rxjava 3.0.9

# 新版本1.7.7

* 解决networkCallback注册，取消注册报错

# 新版本1.7.6

* navigation 2.3.2

# 新版本1.7.5

* 注意：NavHostFragment 移到 androidx.navigation.fragment 路劲下,解决 navArgs() 函数不能用的问题。将 ktx 依赖中对官方 java
  包的依赖排除掉： implementation('androidx.navigation:navigation-fragment-ktx:2.3.1') { exclude group: '
  androidx.navigation', module: "navigation-fragment"
  }

# 新版本1.7.4

* LiveData observe 优化

# 新版本1.7.3

* 重新发布

# 新版本1.7.2

* rxjava 3.0.8
* 解决 DialogFragment 显示报错： Can not perform this action after onSaveInstanceState 问题

# 新版本1.7.1

* kotlin 1.4.20
* gson 2.8.6
* 页面销毁时，binding = null

# 新版本1.7.0

* LiveDataExt 扩展优化

# 新版本1.6.9

* 文件断点下载优化

# 新版本1.6.8

* dagger 2.30.1
* 文件下载支持断点下载

# 新版本1.6.7

* navigation 优化

# 新版本1.6.6

* utilcode 1.30.5
* EventLiveData 优化

# 新版本1.6.5

* navigateUp 优化

# 新版本1.6.4

* utilcode 1.30.4

# 新版本1.6.3

* Glide 加载图片高斯模糊修复
* 支持 Glide 加载部分圆角图片

# 新版本1.5.9

* 点击防抖 isValid 优化

# 新版本1.5.7

* utilcode 1.30.0 → 1.30.1

# 新版本1.5.6

* utilcode 1.29.0 → 1.30.0
* ClipboardUtils 工具类调整

# 新版本1.5.5

* rxjava3 3.0.6 → 3.0.7
* corektx 1.3.1 → 1.3.2
* navigation 2.3.0 → 2.3.1

# 新版本1.5.3

* NavController 跳转优化

# 新版本1.5.2

* Glide 清除缓存优化

# 新版本1.5.1

* kotlin 1.4.0 → 1.4.10
* okhttp 4.8.1 → 4.9.0
* dagger 2.29 → 2.29.1

# 新版本1.5.0

* 修复 FragmentNavigator 数值越界 BUG

# 新版本1.4.9

* 重新发布

# 新版本1.4.8

* rxjava3 3.0.5 → 3.0.6
* androidx_appcompat 1.1.0 → 1.2.0
* constraintlayout 1.1.3 → 2.0.0
* glide 加载调试

# 新版本1.4.7

* okhttp3 4.8.0 → 4.8.1

# 新版本1.4.6

* fly-http：文件下载优化

# 新版本1.4.5

* rxjava3 3.0.4 → 3.0.5
* fly-http：文件下载BUG修复

# 新版本1.4.4

* visible 生命周期优化
* EventLiveData 优化

# 新版本1.4.3

* 注意：Retrofit Map注解：设置(encoded = true)，交由外层自行处理

# 新版本1.4.2

* 移除 Http 输出打印
* createUrlFromParams 函数优化

# 新版本1.4.1

* fly-http：增加 addParamsToUrl 函数，默认为 true，调用 upXxx() 相关函数，并且有传入 urlParams 时，把 urlParams 拼接到 url 上
* EventLiveData 优化

# 新版本1.4.0

* 修复 popUpToInclusive 属性设置 BUG 问题

# 新版本1.3.9

* EventLiveData 添加 isAllowToClear 是否允许自动清理，默认 true
* dagger2 2.28.2 → 2.28.3
* 添加 ViewPager2 动态刷新示例

# 新版本1.3.8

* dagger2 2.28.1 → 2.28.2
* 添加 onCreateInit 函数，可用于初始化前做相关处理，应用被杀死时重启应用改为用户自行处理

# 新版本1.3.7

* 支持应用被杀死时重启应用，重写 isRelaunchApp() 为 true 即可

# 新版本1.3.6

* visible 生命周期优化
* EventLiveData 优化

# 新版本1.3.5

* okhttp3 4.7.2 → 4.8.0
* 修复 Navigation 使用 popUpTo 报错问题

# 新版本1.3.4

* 修复短时间内多次快速跳转 Fragment 出现的 bug

# 新版本1.3.3

* EventLiveData 优化
* navigation 2.2.2 → 2.3.0

# 新版本1.3.2

* dagger2 2.28 → 2.28.1
* kotlin_ktx 1.2.0 → 1.3.0

# 新版本1.3.1

* 移除isInitialized()

# 新版本1.3.0（大改动）

* dagger2 2.27 → 2.28
* utilcode 1.28.6 → 1.29.0
* 依赖调整
* 移除 LiveDataBus，用全局 SharedViewModel.kt 替代
* 添加 EventLiveData.kt 用于替代 UnPeekLiveData 和 SingleLiveEvent，用于解决 LiveData “数据倒灌”问题

# 新版本1.2.6

* 点击防抖优化

# 新版本1.2.5（注意）

* DialogFragment 内存泄漏优化
* 注意：onFlyLazyInitView() 调至 createObserver() 后面比较合理

# 新版本1.2.4

* 点击防抖优化
* Visible 生命周期函数调整优化

# 新版本1.2.3

* utilcode → 1.28.5 → 1.28.6
* 添加全局配置：是否进行全局错误统一处理

# 新版本1.2.2 （大改动）

* RxJava2 → RxJava3
* retrofit2 2.8.1 → 2.9.0
* okhttp3 4.6.0 → 4.7.2
* utilcode 1.28.4 → 1.28.5

# 新版本1.2.1

* BaseFlyDialogFragment 抽象类改为 open

# 新版本1.2.0（大改动）

* 新增 BaseFlyVmDbActivity、BaseFlyVmDbFragment，支持 dataBinding；想使用 dataBinding 时，请在 app 的 build.gradle
  中进行开启

```gradle
android {
    buildFeatures {
        dataBinding = true
    }
}
```

* 添加一些扩展函数
* 注意：此版本改动了一些包结构，需要注意下引用路劲

# 新版本1.1.3

* 支持 dataBinding
* 添加一些扩展函数
* 添加 createObserver() 函数，用于创建观察者

# 新版本1.1.2

* 修复 fragment 返回监听处理失效问题。

# 新版本1.1.1

* ViewPager2 懒加载适配
* 以下几个函数重命名： onLazyInitView() → onFlyLazyInitView()
  onSupportVisible() → onFlySupportVisible()
  onSupportInvisible() → onFlySupportInvisible()
  isSupportVisible(): Boolean → isFlySupportVisible(): Boolean

# 新版本1.1.0

* 类文件冲突处理：androidx.navigation.fragment.NavHostFragment → com.tiamosu.fly.navigation.NavHostFragment

# 新版本1.0.9

* 新增各类服务获取
* 未提供 BaseUrl 时，默认使用 https://api.github.com/

# 新版本1.0.8

* utilcode 1.28.3 → 1.28.4
* 混淆配置优化
* 添加声明： android:hasFragileUserData="true"，Android Q 前提下，应用卸载时会提示用户是否保留 App-specific 目录下的数据。 android:
  requestLegacyExternalStorage="true"，请求使用旧的存储模式

# 新版本1.0.7

* utilcode 1.28.0 → 1.28.3
* okhttp3 4.5.0 → 4.6.0
* Navigation 扩展优化

# 新版本1.0.6（大改版）

* 添加 SingleLiveEvent
* DialogFragment 优化
* 移除 fragmentation 框架，改用 navigation 实现 Fragment 页面导航
* 添加 Navigation 扩展
* 移除 initAny，新增 initParameters、initView、initEvent
* 对 Fragment 显示隐藏逻辑进行优化，支持 fragment 返回监听处理事件

# 新版本1.0.5

* okhttp3 4.4.1 → 4.5.0
* utilcode 1.27.2 → 1.27.6
* fragment 依赖配置

# 新版本1.0.4

* fragmentation 1.1.7 → 1.1.8
* utilcode 1.27.7 → 1.27.2

# 新版本1.0.3

* okhttp 3.12.10 → 4.4.1
* retrofit 2.6.4 → 2.8.1
* fragmentation 1.1.6 → 1.1.7
* utilcode 1.26.0 → 1.27.0
* BaseFlyDialogFragment 优化
* createOrExistsDir(file) 改为 createDir(file)

## Requirements

适用于 Android 5.0 + (21 + API级别) 和 Java 8 +。

# 新版本1.0.2

* rxjava2 2.2.18 → 2.2.19
* 移除 initData()、initView()、initEvent() 方法，由用户自定义处理
* 添加 dataBinding 示例

# 新版本1.0.1

* fragmentation 1.1.5 → 1.1.6
* 添加 fly-livedata-bus 扩展库 `implementation 'me.tiamosu:fly-livedata-bus:1.0.1'`

# 新版本1.0.0

* 第一个版本提交，具体使用请参照 [Wiki](https://github.com/tiamosu/X-MVVMFly/wiki)
