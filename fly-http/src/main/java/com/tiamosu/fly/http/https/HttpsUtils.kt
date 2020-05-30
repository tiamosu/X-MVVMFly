package com.tiamosu.fly.http.https

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

    class SSLParams {
        lateinit var sslSocketFactory: SSLSocketFactory
        lateinit var trustManager: X509TrustManager
    }

    @JvmStatic
    fun getSslSocketFactory(): SSLParams {
        return getSslSocketFactory(null, null, null, null)
    }

    /**
     * https单向认证
     * 可以额外配置信任服务端的证书策略，否则默认是按CA证书去验证的，若不是CA可信任的证书，则无法通过验证
     */
    @JvmStatic
    fun getSslSocketFactory(trustManager: X509TrustManager): SSLParams {
        return getSslSocketFactory(trustManager, null, null, null)
    }

    /**
     * https单向认证
     * 用含有服务端公钥的证书校验服务端证书
     */
    @JvmStatic
    fun getSslSocketFactory(certificates: Array<InputStream>): SSLParams {
        return getSslSocketFactory(null, null, null, certificates)
    }

    /**
     * https双向认证
     * bksFile 和 password -> 客户端使用bks证书校验服务端证书
     * certificates -> 用含有服务端公钥的证书校验服务端证书
     */
    @JvmStatic
    fun getSslSocketFactory(
        bksFile: InputStream,
        password: String,
        certificates: Array<InputStream>
    ): SSLParams {
        return getSslSocketFactory(null, bksFile, password, certificates)
    }

    /**
     * https双向认证
     * bksFile 和 password -> 客户端使用bks证书校验服务端证书
     * X509TrustManager -> 如果需要自己校验，那么可以自己实现相关校验，如果不需要自己校验，那么传null即可
     */
    @JvmStatic
    fun getSslSocketFactory(
        trustManager: X509TrustManager,
        bksFile: InputStream,
        password: String
    ): SSLParams {
        return getSslSocketFactory(trustManager, bksFile, password)
    }

    @JvmStatic
    fun getSslSocketFactory(
        trustManager: X509TrustManager? = null,
        bksFile: InputStream? = null,
        password: String? = null,
        certificates: Array<InputStream>? = null
    ): SSLParams {
        val sslParams = SSLParams()
        val trustManagers = prepareTrustManager(certificates)
        val keyManagers = prepareKeyManager(bksFile, password)

        try {
            //优先使用用户自定义的TrustManager
            val manager = trustManager ?: if (trustManagers != null) {
                //然后使用默认的TrustManager
                MyTrustManager(chooseTrustManager(trustManagers))
            } else {
                //否则使用不安全的TrustManager
                UnSafeTrustManager()
            }

            // 创建TLS类型的SSLContext对象， that uses our TrustManager
            val sslContext = SSLContext.getInstance("TLS")
            // 用上面得到的trustManagers初始化SSLContext，这样sslContext就会信任keyStore中的证书
            // 第一个参数是授权的密钥管理器，用来授权验证，比如授权自签名的证书验证。第二个是被授权的证书管理器，用来验证服务器端的证书
            sslContext.init(keyManagers, arrayOf<TrustManager>(manager), null)
            // 通过sslContext获取SSLSocketFactory对象
            sslParams.sslSocketFactory = sslContext.socketFactory
            sslParams.trustManager = manager
            return sslParams
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        } catch (e: KeyManagementException) {
            throw AssertionError(e)
        } catch (e: KeyStoreException) {
            throw AssertionError(e)
        }
    }

    /**
     * 为了解决客户端不信任服务器数字证书的问题，网络上大部分的解决方案都是让客户端不对证书做任何检查，
     * 这是一种有很大安全漏洞的办法
     */
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
            //创建一个默认类型的KeyStore，存储我们信任的证书
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)

            for ((index, certStream) in certificates.withIndex()) {
                val certificateAlias = index.toString()
                //证书工厂根据证书文件的流生成证书 cert
                val cert = certificateFactory.generateCertificate(certStream)
                //将cert作为可信证书放入到keyStore中
                keyStore.setCertificateEntry(certificateAlias, cert)
                CloseUtils.closeIO(certStream)
            }

            //我们创建一个默认类型的TrustManagerFactory
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            //用我们之前的keyStore实例初始化TrustManagerFactory，这样tmf就会信任keyStore中的证书
            trustManagerFactory.init(keyStore)
            //通过tmf获取TrustManager数组，TrustManager也会信任keyStore中的证书
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
    constructor(private val localTrustManager: X509TrustManager?) : X509TrustManager {
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
                localTrustManager?.checkServerTrusted(chain, authType)
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?> {
            return arrayOfNulls(0)
        }
    }

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    class DefaultHostnameVerifier : HostnameVerifier {
        private val verifyHostNameArray = arrayOf<String>()

        override fun verify(hostname: String, session: SSLSession): Boolean {
            return if (TextUtils.isEmpty(hostname)) {
                false
            } else !listOf(*verifyHostNameArray).contains(hostname)
        }
    }
}
