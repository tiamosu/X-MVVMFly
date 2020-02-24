package com.tiamosu.fly.http.utils

import android.annotation.SuppressLint
import android.text.TextUtils
import com.blankj.utilcode.util.CloseUtils
import java.io.IOException
import java.io.InputStream
import java.security.*
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * @author tiamosu
 * @date 2018/4/2.
 */
object HttpsUtils {

    private val sslParams by lazy {
        getSslSocketFactory(null, null, null)
    }

    @JvmStatic
    val sslSocketFactory by lazy {
        sslParams.sslSocketFactory
    }

    @JvmStatic
    val trustManager by lazy {
        sslParams.trustManager
    }

    @JvmStatic
    val hostnameVerifier by lazy {
        SafeHostnameVerifier()
    }

    @JvmStatic
    fun getSslSocketFactory(
        certificates: Array<InputStream>?,
        bksFile: InputStream?,
        password: String?
    ): SSLParams {
        val sslParams = SSLParams()
        try {
            val trustManagers = prepareTrustManager(certificates)
            val trustManager: X509TrustManager
            trustManager = if (trustManagers != null) {
                MyTrustManager(chooseTrustManager(trustManagers)!!)
            } else {
                UnSafeTrustManager()
            }
            val keyManagers = prepareKeyManager(bksFile, password)
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(keyManagers, arrayOf<TrustManager>(trustManager), SecureRandom())
            sslParams.sslSocketFactory = TLSSocketFactory(sslContext)
            sslParams.trustManager = trustManager
            return sslParams
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        } catch (e: KeyManagementException) {
            throw AssertionError(e)
        } catch (e: KeyStoreException) {
            throw AssertionError(e)
        }
    }

    class SafeHostnameVerifier : HostnameVerifier {
        private val verifyHostNameArray = arrayOf<String>()

        override fun verify(hostname: String, session: SSLSession): Boolean {
            return if (TextUtils.isEmpty(hostname)) {
                false
            } else !listOf(*verifyHostNameArray).contains(hostname)
        }
    }

    private class UnSafeTrustManager : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }

    private fun prepareTrustManager(certificates: Array<InputStream>?): Array<TrustManager>? {
        if (certificates == null || certificates.isEmpty()) {
            return null
        }
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)
            for ((index, certificate) in certificates.withIndex()) {
                val certificateAlias = index.toString()
                keyStore.setCertificateEntry(
                    certificateAlias,
                    certificateFactory.generateCertificate(certificate)
                )
                CloseUtils.closeIO(certificate)
            }
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)
            return trustManagerFactory.trustManagers
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun prepareKeyManager(bksFile: InputStream?, password: String?): Array<KeyManager>? {
        if (bksFile == null || password == null) {
            return null
        }
        try {
            val clientKeyStore = KeyStore.getInstance("BKS")
            clientKeyStore.load(bksFile, password.toCharArray())
            val keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(clientKeyStore, password.toCharArray())
            CloseUtils.closeIO(bksFile)

            return keyManagerFactory.keyManagers
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun chooseTrustManager(trustManagers: Array<TrustManager>): X509TrustManager? {
        for (trustManager in trustManagers) {
            if (trustManager is X509TrustManager) {
                return trustManager
            }
        }
        return null
    }

    private class MyTrustManager @Throws(NoSuchAlgorithmException::class, KeyStoreException::class)
    internal constructor(private val localTrustManager: X509TrustManager) : X509TrustManager {
        private val defaultTrustManager: X509TrustManager?

        init {
            val factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            factory.init(null as KeyStore?)
            defaultTrustManager = chooseTrustManager(factory.trustManagers)
        }

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            try {
                defaultTrustManager?.checkServerTrusted(chain, authType)
            } catch (ce: CertificateException) {
                localTrustManager.checkServerTrusted(chain, authType)
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }
    }

    class SSLParams {
        var sslSocketFactory: SSLSocketFactory? = null
        var trustManager: X509TrustManager? = null
    }
}
