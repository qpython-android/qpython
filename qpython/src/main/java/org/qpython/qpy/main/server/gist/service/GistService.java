package org.qpython.qpy.main.server.gist.service;

import org.qpython.qpy.main.server.gist.request.BaseRequest;
import org.qpython.qpy.main.server.gist.request.CommentRequest;
import org.qpython.qpy.main.server.gist.request.CreateRequest;
import org.qpython.qpy.main.server.gist.request.LoginRequest;
import org.qpython.qpy.main.server.gist.request.UpdateGistRequest;
import org.qpython.qpy.main.server.gist.response.ADBean;
import org.qpython.qpy.main.server.gist.response.CommentBean;
import org.qpython.qpy.main.server.gist.response.GistBean;
import org.qpython.qpy.main.server.gist.response.LoginResponse;
import org.qpython.qpy.main.server.gist.response.ResponseBean;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 文 件 名: GistService
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/8 16:04
 * 修改时间：
 * 修改备注：
 */

public interface GistService {

    @POST("api/login/")
    Observable<ResponseBean<LoginResponse>> login(@Body LoginRequest loginRequest);

    @GET("api/ad")
    Observable<ResponseBean<List<ADBean>>> getAD();

    @GET("api/gist/getgist/")/*{page}*/
    Observable<ResponseBean<List<GistBean>>> getAllGists(/*@Query("page") int page*/);

    @GET("api/gist/mygist/")
    Observable<ResponseBean<List<GistBean>>> getMyGists(@Header("TOKEN") String token);

    @GET("api/gist/myfav")
    Observable<ResponseBean<List<GistBean>>> getMyFavorites(@Header("TOKEN") String token);

    @GET("api/gist/detail/{gist_id}")
    Observable<ResponseBean<GistBean>> getGistDetail(@Path("gist_id") String id);

    @GET("api/gist/comment/{gist_id}/{page}")
    Observable<ResponseBean<List<CommentBean>>> getCommentMore(@Path("gist_id") String id, @Path("page") int page);

    @POST("api/gist/fav/")
    Observable<ResponseBean> favoriteGist(@Header("TOKEN") String token, @Body BaseRequest request);

    @POST("api/gist/comment/")
    Observable<ResponseBean<CommentBean>> commentGist(@Header("TOKEN") String token, @Body
            CommentRequest request);

    @POST("api/gist/fork/")
    Observable<ResponseBean> forkGist(@Header("TOKEN") String token, @Body BaseRequest request);

    @POST("api/gist/delete/")
    Observable<ResponseBean> deleteGist(@Header("TOKEN") String token, @Body BaseRequest request);

    @POST("api/gist/public/")
    Observable<ResponseBean<String>> publishGist(@Header("TOKEN") String token, @Body BaseRequest
            request);

    @POST("api/gist/update/")
    Observable<ResponseBean<String>> updateGist(@Header("TOKEN") String token, @Body
            UpdateGistRequest
            request);

    @POST("api/gist/new/")
    Observable<ResponseBean<String>> createGist(@Header("TOKEN") String token, @Body CreateRequest
            request);

}
