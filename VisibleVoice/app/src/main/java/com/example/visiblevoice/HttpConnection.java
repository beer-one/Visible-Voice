package com.example.visiblevoice;

import java.io.File;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpConnection {

    private static String url = "http://54.180.103.153:8080/upload";
    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();
    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection(){ this.client = new OkHttpClient(); }


    /** 웹 서버로 요청을 한다. */
    public void requestWebServer(File file, Callback callback) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uploadFile", file.getName(), RequestBody.create(MultipartBody.FORM, file))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);

    }

}

