package org.qpython.qpy.main.server.gist;

/**
 * Created by Hmei
 * 4/2/18.
 */

public class GistEvent {
    public static final String FAVORITE_REQUEST   = "FAVORITE_REQUEST";
    public static final String FAVORITE           = "FAVORITE";
    public static final String UNFAVORITE         = "UNFAVORITE";
    public static final String DELETE             = "DELETE";
    public static final String DELETE_SUC         = "DELETE_SUC";
    public static final String COMMENT_EVENT      = "COMMENT_EVENT";
    public static final String FORK               = "FORK";
    public static final String ADD_COMMENT        = "ADD_COMMENT";
    public static final String RUN_SCRIPT         = "RUN_EVENT";
    public static final String RUN_NOTEBOOK       = "RUN_NOTEBOOK";
    public static final String CODE_REVIEW_EVENT  = "CODE_REVIEW_EVENT";
    public static final String REPLY_EVENT        = "REPLY_EVENT";
    public static final String DIVIDER            = "divider_string_distr";
    public static final String UPDATE_GIST_SUC    = "update_gist_suc";
    public static final String REFRESH_COLLECTION = "refresh_collection";


    public String name;
    public String content;

    public GistEvent(String name) {
        this.name = name;
    }

    public GistEvent(String name, String content) {
        this.name = name;
        this.content = content;
    }
}
