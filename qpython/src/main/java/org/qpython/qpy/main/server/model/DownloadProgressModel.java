package org.qpython.qpy.main.server.model;

import android.os.RecoverySystem;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Hmei on 2017-07-07.
 */

public class DownloadProgressModel extends ResponseBody {
    private final ResponseBody                    responseBody;
    private final RecoverySystem.ProgressListener progressListener;
    private       BufferedSource                  bufferedSource;

    public DownloadProgressModel(ResponseBody responseBody, RecoverySystem.ProgressListener progressListener) {
        this.responseBody = responseBody;
        //传入回调
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                //实现回调
                progressListener.onProgress((int) (totalBytesRead / responseBody.contentLength() * 100));
                return bytesRead;
            }
        };
    }
}
