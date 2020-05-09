@file:JvmName("FlyRuntime")

package com.tiamosu.fly.utils

import android.app.*
import android.app.job.JobScheduler
import android.content.ClipboardManager
import android.hardware.SensorManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.PowerManager
import android.os.Vibrator
import android.os.storage.StorageManager
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.Utils

/**
 * @author tiamosu
 * @date 2020/2/22.
 */
inline fun <reified T> getSystemService(): T? =
    ContextCompat.getSystemService(Utils.getApp(), T::class.java)

val windowManager get() = getSystemService<WindowManager>()
val clipboardManager get() = getSystemService<ClipboardManager>()
val layoutInflater get() = getSystemService<LayoutInflater>()
val activityManager get() = getSystemService<ActivityManager>()
val powerManager get() = getSystemService<PowerManager>()
val alarmManager get() = getSystemService<AlarmManager>()
val notificationManager get() = getSystemService<NotificationManager>()
val keyguardManager get() = getSystemService<KeyguardManager>()
val locationManager get() = getSystemService<LocationManager>()
val searchManager get() = getSystemService<SearchManager>()
val storageManager get() = getSystemService<StorageManager>()
val vibrator get() = getSystemService<Vibrator>()
val connectivityManager get() = getSystemService<ConnectivityManager>()
val wifiManager get() = getSystemService<WifiManager>()
val audioManager get() = getSystemService<AudioManager>()
val mediaRouter get() = getSystemService<MediaRouter>()
val telephonyManager get() = getSystemService<TelephonyManager>()
val sensorManager get() = getSystemService<SensorManager>()
val subscriptionManager get() = getSystemService<SubscriptionManager>()
val carrierConfigManager get() = getSystemService<CarrierConfigManager>()
val inputMethodManager get() = getSystemService<InputMethodManager>()
val uiModeManager get() = getSystemService<UiModeManager>()
val downloadManager get() = getSystemService<DownloadManager>()
val batteryManager get() = getSystemService<BatteryManager>()
val jobScheduler get() = getSystemService<JobScheduler>()
val accessibilityManager get() = getSystemService<AccessibilityManager>()