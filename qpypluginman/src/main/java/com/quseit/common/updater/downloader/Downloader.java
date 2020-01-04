package com.quseit.common.updater.downloader;

import java.io.File;

public interface Downloader {
    void download(String name, String url, Callback callback);

    void download(String name, String url, String savePath, Callback callback);

    interface Callback {
        void pending(String name);
        void complete(String name, File installer);
        void error(String err);
    }
}
