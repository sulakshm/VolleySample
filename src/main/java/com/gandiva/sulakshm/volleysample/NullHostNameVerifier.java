package com.gandiva.sulakshm.volleysample;

/**
 * Created by LakshmiNarasimhan on 4/8/2015.
 */

import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class NullHostNameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        Log.i("RestUtilImpl", "Approving certificate for " + hostname);
        return true;
    }

}

