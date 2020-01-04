package org.qpython.qpy.main.server.http;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 文 件 名: RetrofitManager
 * 创 建 人: ZhangRonghua
 * 创建日期: 2017/10/24 11:48
 * 邮   箱: qq798435167@gmail.com
 * 博   客: http://zzzzzzzz3.github.io
 * 修改时间：
 * 修改备注：网络访问封装类
 */

public class Retrofitor {
    private static Retrofitor instance = null;
    private Retrofit mRetrofit = null;
    //超时时间
    public static final int DEFAULT_TIMEOUT = 30;
    private OkHttpClient.Builder mOkhttpBuilder;


    private Retrofitor() {
        mOkhttpBuilder = new OkHttpClient.Builder().retryOnConnectionFailure(true);
    }

    public Retrofitor setTimeOut(int timeOut) {
        mOkhttpBuilder
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS);
        return this;
    }

    public Retrofitor openDebug(boolean debug) {
        if (debug){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            mOkhttpBuilder.addInterceptor(interceptor);
        }
        return this;
    }

    public Retrofitor supportSSL(boolean support) {
        if (support){
            //配置ssl
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                SSLSocketFactoryImp ssl = new SSLSocketFactoryImp(KeyStore.getInstance(KeyStore.getDefaultType()));
                mOkhttpBuilder.sslSocketFactory(ssl.getSSLContext().getSocketFactory(), ssl.getTrustManager());
            } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
            }
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
            mOkhttpBuilder.hostnameVerifier(hostnameVerifier);
            //配置支持的协议
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .allEnabledCipherSuites()
                    .allEnabledTlsVersions()
                    .build();
            mOkhttpBuilder.connectionSpecs(Collections.singletonList(spec));
        }
        return this;
    }

    public Retrofitor addHeaders(final Map<String,String> headers){
        mOkhttpBuilder.addInterceptor(chain -> {
            Request.Builder requestBuilder = chain.request().newBuilder();
            for (String key:headers.keySet()){
                requestBuilder.addHeader(key,headers.get(key));
            }
            return chain.proceed(requestBuilder.build());
        });
        return this;
    }

    /**
     * 初始化retrofit
     */
    public void init(String url) {
        Retrofit.Builder mBuilder = new Retrofit.Builder()
                .baseUrl(url)
                .client(mOkhttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        mRetrofit = mBuilder.build();
    }

    public static Retrofitor getInstance() {
        if (instance == null) {
            synchronized (Retrofitor.class) {
                instance = new Retrofitor();
            }
        }
        return instance;
    }

    /**
     * 获取到接口
     */
    public <T> T createService(Class<T> service) {
        return mRetrofit.create(service);
    }

}
