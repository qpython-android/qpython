package org.qpython.qpy.main.server;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.quseit.util.ACache;
import com.quseit.util.DateTimeHelper;
import com.quseit.util.NAction;

import org.greenrobot.eventbus.EventBus;
import org.qpython.qpy.BuildConfig;
import org.qpython.qpy.main.activity.LibActivity;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.server.model.BaseLibModel;
import org.qpython.qpy.main.server.model.CourseAdModel;
import org.qpython.qpy.main.server.model.CourseModel;
import org.qpython.qpy.main.server.model.LibModel;
import org.qpython.qpy.main.server.model.MyCourse;
import org.qpython.qpy.main.server.model.PayStatusModel;
import org.qpython.qpy.main.server.model.QpypiModel;
import org.qpython.qpy.main.server.model.UpdateModel;
import org.qpython.qpy.main.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static org.qpython.qpy.main.app.CONF.LIB_DOWNLOAD_TEMP;


public class Service extends CacheKey {
    private static final String BASE_URL = "https://";
    private ServiceRequest request;
    private ApuQuseit      apuQuseit;

    public Service() {
        request = App.getRetrofit()
                .baseUrl(BASE_URL + "dl.qpy.io")
                .build()
                .create(ServiceRequest.class);

        apuQuseit = App.getRetrofit()
                .baseUrl(BASE_URL + "apu2.quseit.com/")
                .build()
                .create(ApuQuseit.class);
    }

    /**
     * retrofit 线程管理
     */
    private static <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    public void getLibs(boolean forceRefresh, Subscriber<List<LibModel>> subscriber) {
        List<LibModel> libList = getObject(new TypeToken<ArrayList<LibModel>>() {
        }.getType(), LIB);
        if (libList == null || forceRefresh) {
            toSubscribe(NAction.isQPy3(App.getContext()) ? request.getLibs3() : request.getLibs(), subscriber);
            ACache.get(App.getContext()).put(CacheKey.LIB_LAST_REFRESH, DateTimeHelper.getDate());
        } else {
            toSubscribe(Observable.just(libList), subscriber);
        }
    }

    public void getQPyPi(boolean forceRefresh, Subscriber<List<QpypiModel>> subscriber) {
        List<QpypiModel> qpypiList = getObject(new TypeToken<ArrayList<QpypiModel>>() {
        }.getType(), QPYPI);
        if (qpypiList == null || forceRefresh) {
            toSubscribe(NAction.isQPy3(App.getContext()) ? request.getQPyPi3() : request.getQPyPi(), subscriber);
            ACache.get(App.getContext()).put(CacheKey.QPYPI_LAST_REFRESH, DateTimeHelper.getDate());
        } else {
            toSubscribe(Observable.just(qpypiList), subscriber);
        }
    }

    public void checkUpdate(Subscriber<UpdateModel> subscriber) {
        toSubscribe(request.checkUpdate(), subscriber);
    }

    public Observable<List<CourseModel>> getCourse() {
//        List<CourseModel> courseModels = new ArrayList<>();
//        List<CourseModel> latest = getObject(new TypeToken<ArrayList<CourseModel>>() {
//        }.getType(), COURSE_LATEST);
//        List<CourseModel> recommend = getObject(new TypeToken<ArrayList<CourseModel>>() {
//        }.getType(), COURSE_RECOMMEND);
//        if (latest != null) courseModels.addAll(latest);
//        if (recommend != null) courseModels.addAll(recommend);
//        if (courseModels.size() != 0) {
//            return Observable.just(courseModels);
//        } else {
        EduRequest eduRequest = App.getRetrofit()
                .baseUrl(BASE_URL + "edu.qpython.org")
                .build()
                .create(EduRequest.class);
        if (Utils.isZn()) {
            return eduRequest.getCourseZn();
        } else {
            return eduRequest.getCourse();
        }
//        }
    }

    public void getMyCourse(String email, Subscriber<MyCourse> subscriber) {
        toSubscribe(apuQuseit
                        .getMyCourse(App.getContext().getPackageName(), email),
                subscriber);
    }

    public Observable<CourseAdModel> getCourseAd() {
        return apuQuseit.getCourseAd(BuildConfig.VERSION_CODE);
    }

    public void getArticleSupportNum(String articleId, Subscriber<Object> callback) {
        toSubscribe(apuQuseit.getSupportNum(articleId), callback);
    }

    public void getAIPyList(boolean isQpy3, boolean forceRefresh, Subscriber<List<BaseLibModel>> subscriber) {
        List<BaseLibModel> aipyList = getObject(new TypeToken<ArrayList<BaseLibModel>>() {
        }.getType(), AIPY);
        if (aipyList == null || forceRefresh) {
            toSubscribe(isQpy3?request.getAIPy3():request.getAIPy(), subscriber);
            ACache.get(App.getContext()).put(CacheKey.AIPY_LAST_REFRESH, DateTimeHelper.getDate());
        } else {
            toSubscribe(Observable.just(aipyList), subscriber);
        }
    }

    public void downloadLib(String url, String fileName, String description) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(description);
        request.setTitle(fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(LIB_DOWNLOAD_TEMP, fileName);

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) App.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        LibActivity.DownloadFinishedEvent event = new LibActivity.DownloadFinishedEvent();
        event.queueId = manager.enqueue(request);
        EventBus.getDefault().post(event);
    }

    public void downloadFile(String url, String fileName, String description) {
        downloadFile(App.getContext(), url, fileName, description, null);
    }

    public void downloadFile(Context context, String url, String fileName, String description, String dir) {
        Log.d("LOG", "DOWNLOAD CALLED:"+dir);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(description);
        request.setTitle(fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(dir == null ? Environment.DIRECTORY_DOWNLOADS : dir, fileName);

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) App.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        LibActivity.DownloadFinishedEvent event = new LibActivity.DownloadFinishedEvent();
        try {
            event.queueId = manager.enqueue(request);
            EventBus.getDefault().post(event);
        } catch (IllegalArgumentException e) {
            Toast.makeText(App.getContext(), "Faile to download:"+url, Toast.LENGTH_SHORT).show();
        }
    }

    public void getPayStatus(String account, String sModule, MySubscriber<PayStatusModel> callback) {
        toSubscribe(apuQuseit.getPayStatus(account, sModule), callback);
    }

    private <T> T getObject(Type type, String key) {
        String cacheStr = ACache.get(App.getContext()).getAsString(key);
        return cacheStr == null ? null : App.getGson().fromJson(cacheStr, type);
    }
}
