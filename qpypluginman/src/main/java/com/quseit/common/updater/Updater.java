package com.quseit.common.updater;


import android.app.Application;
import android.content.Context;

import com.quseit.common.updater.convertor.Convertor;
import com.quseit.common.updater.downloader.DefaultDownloader;
import com.quseit.common.updater.downloader.Downloader;
import com.quseit.common.updater.service.DefaultService;
import com.quseit.common.updater.service.Service;
import com.quseit.common.updater.updatepkg.UpdatePackage;

import java.io.File;
import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Updater {
    public static final String TAG = "Updater";
    private static List<UpdatePackage> pkgs;

    private static Service service;
    private static Convertor convertor;
    private static Downloader downloader;

    private static String url;
    private static Context context;

    public static synchronized void init(Application app, String url, Convertor convertor) {
        Updater.context = app.getApplicationContext();
        Updater.url = url;
        Updater.convertor = convertor;

        Updater.service = new DefaultService();
        Updater.downloader = new DefaultDownloader(app.getApplicationContext());
    }

    public static void checkUpdate(final CheckUpdateCallback callback) {
        Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        try {
                            String response = service.request(url);
                            subscriber.onNext(response);
                        } catch (IOException e) {
                            subscriber.onError(e);
                        } finally {
                            subscriber.onCompleted();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(new Func1<String, List<? extends UpdatePackage>>() {
                    @Override
                    public List<? extends UpdatePackage> call(String response) {
                        return convertor.transform(response);
                    }
                })
                .flatMap(new Func1<List<? extends UpdatePackage>, Observable<UpdatePackage>>() {
                    @Override
                    public Observable<UpdatePackage> call(List<? extends UpdatePackage> updatePkgs) {
                        return Observable.from(updatePkgs);
                    }
                })
                .filter(new Func1<UpdatePackage, Boolean>() {
                    @Override
                    public Boolean call(UpdatePackage updatePackage) {
                        return updatePackage.checkVersion();
                    }
                })
                .toList()
                .doOnNext(new Action1<List<UpdatePackage>>() {
                    @Override
                    public void call(List<UpdatePackage> pkgs) {
                        // 保存最新的获取的更新包
                        Updater.pkgs = pkgs;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<UpdatePackage>>() {
                            @Override
                            public void call(List<UpdatePackage> updatePackages) {
                                if (!updatePackages.isEmpty()) {
                                    callback.hasUpdate(updatePackages);
                                } else {
                                    callback.noneUpdate();
                                }
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                                callback.error(throwable);
                            }
                        });
    }

    public static void update(UpdatePackage pkg) {
        downloadAndInstall(pkg);
    }

    public static void update() {
        // 检查是否调用过 checkUpdate
        if (Updater.pkgs == null) {
            return;
        }

        downloadAndInstall(pkgs);
    }

    private static void downloadAndInstall(final UpdatePackage pkg) {
        downloader.download(pkg.getName(), pkg.getDownloadUrl(),
                new Downloader.Callback() {
                    @Override
                    public void complete(String name, File installer) {
                        pkg.install(installer);
                    }
                });
    }

    private static void downloadAndInstall(List<UpdatePackage> pkgs) {
        for (UpdatePackage pkg : pkgs) {
            downloadAndInstall(pkg);
        }
    }

    public static Context getContext() {
        return context;
    }

    public interface CheckUpdateCallback {
        void hasUpdate(List<UpdatePackage> pkgs);

        void noneUpdate();

        void error(Throwable e);
    }
}
