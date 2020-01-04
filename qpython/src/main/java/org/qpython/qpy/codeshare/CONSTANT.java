package org.qpython.qpy.codeshare;


public class CONSTANT {
    public static final String IS_UPLOAD_INIT = "is_upload_init";// 是否同步了云端文件名
    public static final String CLOUDED_MAP    = "clouded_map";// 云端文件名路径MAP
    public static final String DOT_REPLACE    = "_%"; // Firebase Database中"." & "/" 等为非法字符无法上传, 需转义
    public static final String SLASH_REPLACE  = "-%";
}
