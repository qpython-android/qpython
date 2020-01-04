package org.qpython.qpy.main.server;

import org.qpython.qpy.main.server.model.BaseLibModel;
import org.qpython.qpy.main.server.model.CourseAdModel;
import org.qpython.qpy.main.server.model.CourseModel;
import org.qpython.qpy.main.server.model.LibModel;
import org.qpython.qpy.main.server.model.MyCourse;
import org.qpython.qpy.main.server.model.PayStatusModel;
import org.qpython.qpy.main.server.model.QpypiModel;
import org.qpython.qpy.main.server.model.UpdateModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Requests
 * Created by Hmei on 2017-06-01.
 */

interface ServiceRequest {
    /**
     * Domain: http://dl.qpy.io
     */

    @GET("/libs-2x-715.json")
    Observable<List<LibModel>> getLibs();

    @GET("/libs-3x-66.json")
    Observable<List<LibModel>> getLibs3();

    @GET("/qpypi-2x-715.json")
    Observable<List<QpypiModel>> getQPyPi();

    @GET("/qpypi-3x-66.json")
    Observable<List<QpypiModel>> getQPyPi3();

    @GET("/aipy-2x-715.json")
    Observable<List<BaseLibModel>> getAIPy();

    @GET("/aipy-3x-66.json")
    Observable<List<BaseLibModel>> getAIPy3();

    @GET("/update.json")
    Observable<UpdateModel> checkUpdate();

    @GET("/courses-2x.json ")
    Observable<List<CourseModel>> getCourse();


}

interface ApuQuseit {
    /**
     * Domain: http://apu.quseit.com/
     */

    @GET("/conf/update/org.qpython.qpy/{verCode}")
    Observable<CourseAdModel> getCourseAd(@Path("verCode") int verCode);

    @GET("conf/iaplognum/org.qpython.qpy/{articleId}")
    Observable<Object> getSupportNum(@Path("articleId") String articleId);

    @GET("/iap/items/{packageId}/{email}")
    Observable<MyCourse> getMyCourse(@Path("packageId") String packageId, @Path("email") String email);

    @GET("/iap/hasitem/org.qpython.qpy/{email}/{smodule}/")
    Observable<PayStatusModel> getPayStatus(@Path("email") String email, @Path("smodule") String
            smodule);
}

interface EduRequest {
    /**
     * Domain : http://edu.qpython.org
     */

    @GET("/index/default.json")
    Observable<List<CourseModel>> getCourse();

    @Deprecated
    @GET("/index/zh.json")
    Observable<List<CourseModel>> getZnCourse();

    @GET("/index/zhv2.json")
    Observable<List<CourseModel>> getCourseZn();
}
