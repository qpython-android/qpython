package com.quseit.common.updater.updatepkg;

import java.io.File;

public interface UpdatePackage {

    String getName();

    String getVersion();

    String getVersionDescription();

    String getDownloadUrl();

    boolean checkVersion();

    void install(File file);
}
