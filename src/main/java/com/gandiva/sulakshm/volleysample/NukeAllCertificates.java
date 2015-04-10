package com.gandiva.sulakshm.volleysample;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by LakshmiNarasimhan on 4/9/2015.
 */
public class NukeAllCertificates extends SSLSocketFactory {

    private static SSLContext sslContext;

    public SSLSocketFactory getSslSocketFactory(KeyStore ignored) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        if (sslContext != null) return  sslContext.getSocketFactory();

        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] {tm}, null);
        return sslContext.getSocketFactory();
    }

    /**
     * Returns the names of the cipher suites that are enabled by default.
     *
     * @return the names of the cipher suites that are enabled by default.
     */
    @Override
    public String[] getDefaultCipherSuites() {
        return sslContext.getServerSocketFactory().getDefaultCipherSuites();
    }

    /**
     * Returns the names of the cipher suites that are supported and could be
     * enabled for an SSL connection.
     *
     * @return the names of the cipher suites that are supported.
     */
    @Override
    public String[] getSupportedCipherSuites() {
        return sslContext.getServerSocketFactory().getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }

    /**
     * Creates a new socket which is connected to the remote host specified by
     * the parameters {@code host} and {@code port}. The socket is bound to any
     * available local address and port.
     *
     * @param host the remote host address the socket has to be connected to.
     * @param port the port number of the remote host at which the socket is
     *             connected.
     * @return the created connected socket.
     * @throws java.io.IOException           if an error occurs while creating a new socket.
     * @throws java.net.UnknownHostException if the specified host is unknown or the IP address could not
     *                                       be resolved.
     */
    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(host, port);
    }

    /**
     * Creates a new socket which is connected to the remote host specified by
     * the parameters {@code host} and {@code port}. The socket is bound to the
     * local network interface specified by the InetAddress {@code localHost} on
     * port {@code localPort}.
     *
     * @param host      the remote host address the socket has to be connected to.
     * @param port      the port number of the remote host at which the socket is
     *                  connected.
     * @param localHost the local host address the socket is bound to.
     * @param localPort the port number of the local host at which the socket is
     *                  bound.
     * @return the created connected socket.
     * @throws java.io.IOException           if an error occurs while creating a new socket.
     * @throws java.net.UnknownHostException if the specified host is unknown or the IP address could not
     *                                       be resolved.
     */
    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(host, port, localHost, localPort);
    }

    /**
     * Creates a new socket which is connected to the remote host specified by
     * the InetAddress {@code host}. The socket is bound to any available local
     * address and port.
     *
     * @param host the host address the socket has to be connected to.
     * @param port the port number of the remote host at which the socket is
     *             connected.
     * @return the created connected socket.
     * @throws java.io.IOException if an error occurs while creating a new socket.
     */
    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return sslContext.getSocketFactory().createSocket(host, port);
    }

    /**
     * Creates a new socket which is connected to the remote host specified by
     * the InetAddress {@code address}. The socket is bound to the local network
     * interface specified by the InetAddress {@code localHost} on port {@code
     * localPort}.
     *
     * @param address      the remote host address the socket has to be connected to.
     * @param port         the port number of the remote host at which the socket is
     *                     connected.
     * @param localAddress the local host address the socket is bound to.
     * @param localPort    the port number of the local host at which the socket is
     *                     bound.
     * @return the created connected socket.
     * @throws java.io.IOException if an error occurs while creating a new socket.
     */
    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return sslContext.getSocketFactory().createSocket(address, port, localAddress, localPort);
    }
}