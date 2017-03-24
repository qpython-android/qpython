package com.quseit.common.updater.downloader;

import java.io.File;

public interface Downloader {
    void download(String name, String url, Callback callback);

    interface Callback {
        void complete(String name, File installer);
    }
}
