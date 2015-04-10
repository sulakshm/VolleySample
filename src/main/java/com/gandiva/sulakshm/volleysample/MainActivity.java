package com.gandiva.sulakshm.volleysample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


public class MainActivity extends ActionBarActivity implements Response.Listener<JSONObject>,
        Response.ErrorListener {
    public static final String REQUEST_TAG = "MainVolleyActivity";
    static final String device_id = "54321";
    static final String base_url_fmt = "https://%s/mobileapi/1/";
    private static final int MY_SOCKET_TIMEOUT_MS = 20000;
    static String base_url;
    static String HostAddr;
    static String secret_key = "";
    private static String[] requestArray = {"Login", "Logout", "Get Items", "MkDir", "Delete", "Upload", "Download", "Get Items Version"};
    private TextView mResponseView;
    private EditText mHostAddr, mRequestArg;
    private RequestQueue mQueue;

    private static boolean buildbasicUrl(String hostAddr) {
        if (hostAddr.isEmpty()) {
            return false;
        }

        HostAddr = hostAddr;
        base_url = String.format(Locale.US, base_url_fmt, hostAddr);

        return true;
    }


    private void createLoginRequest() {
        if (!buildbasicUrl(mHostAddr.getText().toString())) {
            Toast.makeText(MainActivity.this, "Invalid Host Address", Toast.LENGTH_SHORT).show();
            return;
        }
        String login_url = base_url + "auth/login";

//                JSONObject params = new JSONObject();
        HashMap<String, String> params = new HashMap<>();
        params.put("device_id", device_id);
        params.put("device_type", "Android");
        params.put("username", "msys/nasuni");
        params.put("password", "n@ssw0rd");

/*
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                        login_url, params, listener, errorListener);
                jsonRequest.setTag(REQUEST_TAG);
*/

        CustomRequest mRequest = new CustomRequest(Request.Method.POST,
                login_url, params, this, this);
        CustomRequest.setLogin_active(device_id);
        mResponseView.setText("Login in progress...");

        mQueue = getRequestQueue();
        mQueue.add(mRequest);
    }

    private void createLogoutRequest() {
        if (HostAddr == null || HostAddr.isEmpty()) {
            Toast.makeText(this, "Login request is not complete.", Toast.LENGTH_SHORT).show();
            return;
        }

        String logout_url = base_url + "auth/logout";

        CustomRequest mRequest = new CustomRequest(Request.Method.POST, logout_url, null, this, this);
        CustomRequest.setLogout_active();
        mResponseView.setText("Logout in progress...");


        mQueue = getRequestQueue();
        mQueue.add(mRequest);
    }

    private void createGetItemsRequest(boolean revision) {
        if (HostAddr == null || HostAddr.isEmpty()) {
            Toast.makeText(this, "Login request is not complete.", Toast.LENGTH_SHORT).show();
            return;
        }

        String getitems_url = base_url + "fs";

        String item = mRequestArg.getText().toString();
        if (!item.isEmpty()) {
            getitems_url += "/" + item;
        }

        HashMap<String, String> params = new HashMap<>();
        if (revision) {
            params.put("previous", "true");
        }

        CustomRequest mRequest = new CustomRequest(Request.Method.GET,
                getitems_url, (revision ? params : null), this, this);

        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mResponseView.setText("Get Items in progress...");
        mQueue = getRequestQueue();
        mQueue.add(mRequest);
    }

    private void createMkDirRequest() {
        if (HostAddr == null || HostAddr.isEmpty()) {
            Toast.makeText(this, "Login request is not complete.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = base_url + "fs";
        String item = mRequestArg.getText().toString();
        if (!item.isEmpty()) {
            url += "/" + item;
        }
        url += "?action=mkdir";

        CustomRequest mRequest = new CustomRequest(Request.Method.POST,
               url, null, this, this);

        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mResponseView.setText("Mkdir url ..." + url);
        mQueue = getRequestQueue();
        mQueue.add(mRequest);
    }

    private void createUploadRequest (String path, String file, String contents) {
        if (HostAddr == null || HostAddr.isEmpty()) {
            Toast.makeText(this, "Login request is not complete.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = base_url + "fs";
        String item = mRequestArg.getText().toString();
        if (!item.isEmpty()) {
            url += "/" + item;
        }

        item += '/' + file; /* append leaf file into directory path */

        CustomRequest mRequest = new CustomRequest(Request.Method.PUT,
                url, null, this, this);

        mRequest.setBodyContent(contents);

        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mResponseView.setText("Upload url ..." + url);
        mQueue = getRequestQueue();
        mQueue.add(mRequest);
    }

    private void createDeleteRequest() {
        if (HostAddr == null || HostAddr.isEmpty()) {
            Toast.makeText(this, "Login request is not complete.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = base_url + "fs";
        String item = mRequestArg.getText().toString();
        if (!item.isEmpty()) {
            url += "/" + item;
        }

        CustomRequest mRequest = new CustomRequest(Request.Method.DELETE,
                url, null, this, this);

        mRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mResponseView.setText("Delete url ..." + url);
        mQueue = getRequestQueue();
        mQueue.add(mRequest);
    }

/*
    static final String basic_params_fmt = "device_id=%s&X-Secret-Key=%s";
    static String get_basic_params() {
        return String.format(Locale.US, basic_params_fmt, device_id, secret_key);
    }*/

    private void setupTrustHosts() {
        HttpsURLConnection.setDefaultSSLSocketFactory(MySSLSocketFactory.getSSLSocketFactory(this));
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    private synchronized RequestQueue getRequestQueue() {
        if (mQueue == null) {

            mQueue = Volley.newRequestQueue(this);

            setupTrustHosts();
//            Cache cache = new DiskBasedCache(this.getCacheDir(), 10 * 1024 * 1024);
//            Network network = new BasicNetwork(new HurlStack(null, MySSLSocketFactory.getSSLSocketFactory(this)));
//            Network network = new BasicNetwork(new HurlStack());
//            Network network = new BasicNetwork(new HttpClientStack(new DefaultHttpClient()));
//            mQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mQueue.start();
        }
        return mQueue;
    }


    private void handleRequest(int which, String reqCode) {

        // "Get Items", "MkDir", "Delete", "Upload", "Download"
        switch (which) {
            case 0: /* Login */
                createLoginRequest();
                break;
            case 1: /* Logout */
                createLogoutRequest();
                break;
            case 2: /* GetItems */
                createGetItemsRequest(false);
                break;
            case 3: /* MkDir */
                createMkDirRequest();
                break;
            case 4: /* Delete */
                createDeleteRequest();
                break;
            case 5: /* Upload */
                FilePicker.loadFileList(this);
                FilePicker.showDialog(this, FilePicker.DIALOG_LOAD_FILE, new FilePickerResponse() {
                    @Override
                    public void onSuccess(String path, String file) {
                        Toast.makeText(MainActivity.this, "Upload file: " + file, Toast.LENGTH_SHORT).show();
                        File chosenFile = new File(path + '/' + file);
                        if (chosenFile.exists() && chosenFile.canRead()) {
                            try {
                                FileInputStream in = new FileInputStream(chosenFile);
                                byte[] data = new byte[(int) chosenFile.length()];
                                in.read(data);
                                in.close();

                                showToast("File length: " + chosenFile.length());

                                /* now push this data to the filer */
                                createUploadRequest(path, file, new String(data));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });

                break;
            case 6: /* Download */
                createGetItemsRequest(false);
                break;
            case 7: /* Get Item Version */
                createGetItemsRequest(true);
                break;
            default: /* unexpected, unknown reqCode */
                Toast.makeText(this, "Unknown request: " + reqCode, Toast.LENGTH_SHORT).show();
        }
    }

    void showToast (String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHostAddr = (EditText) findViewById(R.id.HostAddrVal);
        mRequestArg = (EditText) findViewById(R.id.RequestArg);
        mResponseView = (TextView) findViewById(R.id.responseView);
        Button mRequestButton = (Button) findViewById(R.id.request_button);
        final Response.Listener<JSONObject> listener = this;
        final Response.ErrorListener errorListener = this;

        mRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setSingleChoiceItems(requestArray, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleRequest(which, requestArray[which]);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (mQueue != null) {
            mQueue.cancelAll(REQUEST_TAG);
        }
        super.onDestroy();

    }

    /**
     * Callback method that an error has been occurred with the
     * provided error code and optional user-readable message.
     *
     * @param error Volley error message
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        mResponseView.setText("Error: " + error.getMessage());
    }

    /**
     * Called when a response is received.
     *
     * @param resp Volley network response
     */
    @Override
    public void onResponse(JSONObject resp) {
        Log.d(REQUEST_TAG, "Response is: " + resp.toString());

        mResponseView.setText("Response: " + resp.toString());

        if (resp.has("X-Secret-Key")) {
            try {
                secret_key = (String) resp.get("X-Secret-Key");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}