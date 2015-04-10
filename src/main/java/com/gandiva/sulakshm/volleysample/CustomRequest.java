package com.gandiva.sulakshm.volleysample;

/**
 * Created by LakshmiNarasimhan on 4/9/2015.
 */

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomRequest extends Request<JSONObject> {

    private Listener<JSONObject> listener;
    private Map<String, String> params;

    private String contents;

    private static boolean login_active = false;
    private static boolean logout_active = false;

    private static boolean validcreds = false;
    private static String username = "";
    private static String password = "";

    public static void setLogin_active (String uname) {
        login_active = true;
        username = uname;
    }

    public static void setLogout_active () {
        logout_active = true;
    }

    public static void setLoggedIn (boolean result, String uname, String passwd) {
        validcreds = result;
        login_active = false;

        if (validcreds) {
            username = uname;
            password = passwd;
        }
    }

    public static void clearLoggedIn () {
        validcreds = false;
        logout_active = false;
    }

    public CustomRequest(String url, Map<String, String> params, Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.contents = null;
        this.listener = reponseListener;
        this.params = params;
    }

    public CustomRequest(int method, String url, Map<String, String> params, Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.contents = null;
        this.listener = reponseListener;
        this.params = params;
    }

    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return params;
    }

    public void setBodyContent (String content) {
        contents = new String(content);
    }

    @Override
    public String getBodyContentType() {
        if (contents != null) {
            return "application/raw; charset=" + getParamsEncoding();
        }

        return super.getBodyContentType();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (contents != null) {
            return contents.getBytes();
        }
        return super.getBody();
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (validcreds) {
            HashMap<String, String> hparams = new HashMap<String, String>();
            String creds = String.format("%s:%s", username, password);
            String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
            hparams.put("Authorization", auth);
            Log.d("CustomRequest:", "Authorization: " + auth);
            return hparams;
        }

        return Collections.emptyMap();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {

        try {
            String dataString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONObject resJson = new JSONObject(response.headers);
            resJson.put("data", dataString);

            if (login_active) {
                if (resJson.has("X-Secret-Key")) {
                    password = resJson.getString("X-Secret-Key");
                    validcreds = true;
                }
                login_active = false;
            } else if (logout_active) {
                validcreds = false;
                logout_active = false;
            }
            return Response.success(resJson, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        if (login_active || logout_active) {
            login_active = false;
            validcreds = false;
        }
        listener.onResponse(response);
    }
}
