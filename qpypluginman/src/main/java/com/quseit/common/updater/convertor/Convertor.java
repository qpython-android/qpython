package com.quseit.common.updater.convertor;


import com.quseit.common.updater.updatepkg.UpdatePackage;

import java.util.List;

public interface Convertor {
    List<? extends UpdatePackage> transform(String response);
}
