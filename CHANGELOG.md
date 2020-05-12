# 新版本1.1.1
* ViewPager2 懒加载适配

# 新版本1.1.0
* 类文件冲突处理：androidx.navigation.fragment.NavHostFragment → com.tiamosu.fly.navigation.NavHostFragment

# 新版本1.0.9
* 新增各类服务获取
* 未提供 BaseUrl 时，默认使用 https://api.github.com/

# 新版本1.0.8
* utilcode 1.28.3 → 1.28.4
* 混淆配置优化
* 添加声明：
  android:hasFragileUserData="true"，Android Q 前提下，应用卸载时会提示用户是否保留 App-specific 目录下的数据。
  android:requestLegacyExternalStorage="true"，请求使用旧的存储模式

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
