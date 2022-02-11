package ir.amozkade.advancedAsisstiveTouche.helper.api

import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.*

internal class CustomSSLSocketFactory(x509Trust: X509TrustManager) : SSLSocketFactory() {
    private var sslSocketFactory: SSLSocketFactory
    override fun getDefaultCipherSuites(): Array<String> {
        return sslSocketFactory.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return sslSocketFactory.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket {
        val socket = sslSocketFactory.createSocket(s, host, port, autoClose) as SSLSocket
        return enableTLSOnSocket(socket)
    }

    @Throws(IOException::class)
    override fun createSocket(host: String, port: Int): Socket {
        val socket = sslSocketFactory.createSocket(host, port) as SSLSocket
        return enableTLSOnSocket(socket)
    }

    @Throws(IOException::class)
    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket {
        val socket = sslSocketFactory.createSocket(host, port, localHost, localPort) as SSLSocket
        return enableTLSOnSocket(socket)
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket {
        val socket = sslSocketFactory.createSocket(host, port) as SSLSocket
        return enableTLSOnSocket(socket)
    }

    @Throws(IOException::class)
    override fun createSocket(inetAddress: InetAddress, i: Int, inetAddress2: InetAddress, i2: Int): Socket {
        val socket = sslSocketFactory.createSocket(inetAddress, i, inetAddress2, i2) as SSLSocket
        return enableTLSOnSocket(socket)
    }

    private fun enableTLSOnSocket(socket: Socket): Socket {
        if (socket is SSLSocket) {
            socket.enabledProtocols = arrayOf("SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2")
        } else {
            (socket as SSLSocket).enabledProtocols = arrayOf("SSLv3", "TLSv1")
        }
        return socket
    }

    init {
        val context = SSLContext.getInstance("TLS")
        context.init(null, arrayOf<TrustManager>(x509Trust), null)
        sslSocketFactory = context.socketFactory
    }
}