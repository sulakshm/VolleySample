package com.gandiva.sulakshm.volleysample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class MainActivity extends ActionBarActivity implements Response.Listener,
        Response.ErrorListener {
    public static final String REQUEST_TAG = "MainVolleyActivity";
    private TextView mTextView;

    private Button mLoginButton, mLogoutButton, mGetButton;


    private RequestQueue mQueue;
    static final String url = "http://api.openweathermap.org/data/2.5/weather?q=London,uk";

    static final String device_id = "12345";
    static final String base_url = "https://172.30.38.160/mobileapi/1/";


    static final String login_url = base_url + "auth/login";
    static final String logout_url = base_url + "auth/logout";
    static final String getitems_url = base_url + "fs/ns";

    static String secret_key = "";
    static final String basic_params = "device_id=12345&X-Secret-Key=";

    static String get_basic_params() {
        return basic_params + secret_key;
    }

    private SSLSocketFactory getSSLSocketFactory () {

        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance("BKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        InputStream in = getResources().openRawResource(R.raw.openssl_certificate);
        try {
            trustStore.load(in, "test123".toCharArray());
            in.close();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SSLSocketFactory sf = null;
        try {
            sf = (SSLSocketFactory) new MySSLSocketFactory(trustStore);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

        return sf;
    }

    private synchronized RequestQueue getRequestQueue() {
        if (mQueue == null) {
            Cache cache = new DiskBasedCache(this.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack(null, getSSLSocketFactory()));
            mQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mQueue.start();
        }
        return mQueue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mLogoutButton = (Button) findViewById(R.id.logout_button);
        mGetButton = (Button) findViewById(R.id.getitems_button);
        final Response.Listener<JSONObject> listener = this;
        final Response.ErrorListener errorListener = this;

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                try {
                    params.put("device_id", "12345");
                    params.put("device_type", "Android");
                    params.put("username", "msys/nasuni");
                    params.put("password", "n@ssw0rd");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                        login_url, params, listener, errorListener);
                jsonRequest.setTag(REQUEST_TAG);
                mQueue = getRequestQueue();
                mQueue.add(jsonRequest);
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                try {
                    params.put("device_id", "12345");
                    params.put("X-Secret-Key", secret_key);
                    params.put("username", "msys/nasuni");
                    params.put("password", "n@ssw0rd");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                        logout_url, params, listener, errorListener);
                jsonRequest.setTag(REQUEST_TAG);

                mQueue = getRequestQueue();
                mQueue.add(jsonRequest);
            }
        });

        mGetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                try {
                    params.put("device_id", "12345");
                    params.put("X-Secret-Key", secret_key);
                    params.put("username", "msys/nasuni");
                    params.put("password", "n@ssw0rd");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                        getitems_url, params, listener, errorListener);
                jsonRequest.setTag(REQUEST_TAG);

                mQueue = getRequestQueue();
                mQueue.add(jsonRequest);
            }
        });

    }

    @Override
    public void onDestroy() {
        mQueue.cancelAll(REQUEST_TAG);
        super.onDestroy();

    }

    /**
     * Callback method that an error has been occurred with the
     * provided error code and optional user-readable message.
     *
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        mTextView.setText(error.getMessage());
    }

    /**
     * Called when a response is received.
     *
     * @param response
     */
    @Override
    public void onResponse(Object response) {
        mTextView.setText("Response is: " + response);
        try {
            mTextView.setText(mTextView.getText() + "\n\n" + ((JSONObject) response).getString
                    ("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
