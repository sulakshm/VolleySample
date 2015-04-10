
package com.gandiva.sulakshm.volleysample;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;


public class MySSLSocketFactory {
    protected static final String TAG = "CustomSSLSocketFactory";
    protected static SSLContext sslContext;

/*    @Override
    public void setEnabledProtocols(String[] protocols) {
        if (protocols != null && protocols.length == 1 && "SSLv3".equals(protocols[0])) {
            // no way jose
            // see issue https://code.google.com/p/android/issues/detail?id=78187
            List<String> enabledProtocols = new ArrayList<String>(Arrays.asList(sslContext.getSocketFactory().getEnabledProtocols()));
            if (enabledProtocols.size() > 1) {
                enabledProtocols.remove("SSLv3");
            }
            protocols = enabledProtocols.toArray(new String[enabledProtocols.size()]);
        }
        super.setEnabledProtocols(protocols);
    }*/

    public static SSLSocketFactory getSSLSocketFactory(final Context context) {
        try {
            if (sslContext == null) {
/*
                // Load CAs from an InputStream
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                InputStream inputStream = context.getResources().openRawResource(R.raw.openssl_certificate);
                InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
                java.security.cert.Certificate ca;
                try {
                    ca = cf.generateCertificate(inputStream);
                    Log.d(TAG, "ca=" + ((X509Certificate) ca).getSubjectDN());
                } finally {
                    inputStream.close();
                }
*/


                KeyStore ks = KeyStore.getInstance("BKS");
                InputStream inputStream = context.getResources().openRawResource(R.raw.myserver);
                Log.d(TAG, "Available bytes: " + inputStream.available());
                ks.load(inputStream, context.getString(R.string.store_pass).toCharArray());
                inputStream.close();
                Log.d(TAG, "KeyStore size: " + ks.size());
/*
// Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore ks = KeyStore.getInstance(keyStoreType);
                ks.load(null, null);
                ks.setCertificateEntry("ca", ca);
*/

                String algo = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(algo);
                tmf.init(ks);
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, tmf.getTrustManagers(), null);
/*
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{new MyTrustManager(ks)}, null);
*/
/*
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });*/
            }
        } catch (KeyStoreException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (KeyManagementException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (IOException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }

        if (sslContext == null)
            throw new RuntimeException("sslContext still null!!");

        return sslContext.getSocketFactory();
    }
}