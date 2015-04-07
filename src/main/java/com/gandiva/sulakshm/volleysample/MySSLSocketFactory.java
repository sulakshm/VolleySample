
package com.gandiva.sulakshm.volleysample;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.content.Context;
import android.util.Log;

import com.gandiva.sulakshm.volleysample.R;

public class MySSLSocketFactory {
    protected static final String TAG = "CustomSSLSocketFactory";

    public static SSLSocketFactory getSSLSocketFactory(final Context context) {
        SSLSocketFactory ret = null;

        try {
            final KeyStore ks = KeyStore.getInstance("BKS");
            final InputStream inputStream = context.getResources().openRawResource(R.raw.my);
            ks.load(inputStream, context.getString(R.string.store_pass).toCharArray());
            inputStream.close();
            ret = new SSLSocketFactory(ks);
        } catch (UnrecoverableKeyException ex) {
            Log.d(TAG, ex.getMessage());
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

        return ret;
    }
}