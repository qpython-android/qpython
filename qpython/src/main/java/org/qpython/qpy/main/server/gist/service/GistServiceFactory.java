package org.qpython.qpy.main.server.gist.service;

import org.qpython.qpy.main.server.http.Retrofitor;

/**
 * 文 件 名: GistServiceFactory
 * 创 建 人: ZhangRonghua
 * 创建日期: 2018/3/8 16:04
 * 修改时间：
 * 修改备注：
 */

public class GistServiceFactory {

    public static GistService gist(){
        return Retrofitor.getInstance().createService(GistService.class);
    }
}
