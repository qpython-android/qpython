package com.quseit.common.updater.service;

import java.io.IOException;

public interface Service {
    String request(String url) throws IOException;
}
