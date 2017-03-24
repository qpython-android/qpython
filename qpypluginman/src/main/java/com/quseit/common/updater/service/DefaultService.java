package com.quseit.common.updater.service;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DefaultService implements Service {
    private final OkHttpClient client;

    public DefaultService() {
        client = new OkHttpClient();
    }

    @Override
    public String request(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return client.newCall(request).execute().body().string();
    }
}
