package com.qqonline.conmon;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * Created by YE on 2015/10/10 0010.
 */
public class HttpUtil {
    private static AsyncHttpClient client = new AsyncHttpClient();

    private static SyncHttpClient syncHttpClient = new SyncHttpClient();

    public static void getSync(String url,ResponseHandlerInterface responseHandlerInterface) {
        syncHttpClient.get(url,responseHandlerInterface);
    }
    //get
    public static void get(String url, BinaryHttpResponseHandler handler) {
        client.get(url, handler);
    }
    public static void get(String url, AsyncHttpResponseHandler handler) {
        client.get(url, handler);
    }
    public static void get(String url, TextHttpResponseHandler handler) {
        client.get(url, handler);
    }

    public static void get(String url, JsonHttpResponseHandler handler) {
        client.get(url, handler);
    }

    public static void get(String url, RequestParams params, TextHttpResponseHandler handler) {
        client.get(url, params, handler);
    }
    public static void get(String url, RequestParams params,  JsonHttpResponseHandler handler) {
        client.get(url,params, handler);
    }
    public static void get(String url,RequestParams params, BinaryHttpResponseHandler handler) {
        client.get(url, params,handler);
    }
    public static void get(String url,RequestParams params, AsyncHttpResponseHandler handler) {
        client.get(url,params, handler);
    }
    //post
    public static void post(String url, BinaryHttpResponseHandler handler) {
        client.post(url, handler);
    }
    public static void post(String url, TextHttpResponseHandler handler) {
        client.post(url, handler);
    }
    public static void post(String url, JsonHttpResponseHandler handler) {
        client.post(url, handler);
    }
    public static void post(String url, AsyncHttpResponseHandler handler) {
        client.post(url, handler);
    }
    public static void post(String url, RequestParams params,TextHttpResponseHandler handler) {
        client.post(url,params, handler);
    }
    public static void post(String url, RequestParams params,JsonHttpResponseHandler handler) {
        client.post(url,params, handler);
    }
    public static void post(String url, RequestParams params,BinaryHttpResponseHandler handler) {
        client.post(url,params, handler);
    }
    public static void post(String url, RequestParams params,AsyncHttpResponseHandler handler) {
        client.post(url,params, handler);
    }

    public static void setConnectingTimeout(int timeout) {
        client.setConnectTimeout(timeout);
    }

    public static void setResponseTimeout(int timeout) {
        client.setResponseTimeout(timeout);
    }
}
