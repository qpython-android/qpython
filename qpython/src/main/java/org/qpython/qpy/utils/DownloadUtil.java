package org.qpython.qpy.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import org.qpython.qpy.R;

import java.io.File;

/**
 * @ProjectName: qpython
 * @Package: org.qpython.qpy.utils
 * @ClassName: DownloadUtil
 * @Description: 下载应用类
 * @Author: wjx
 * @CreateDate: 2022/1/11 16:39
 * @Version: 1.0
 */
public class DownloadUtil {
    public static void startDownloader(Context context, String url, String fileName, String mimeType,
                                               String title, String description) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            Uri uri = Uri.parse(url);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            // 在通知栏中显示
            request.setVisibleInDownloadsUi(true);
            if(!TextUtils.isEmpty(title)) {
                request.setTitle(title);
            } else {
                request.setTitle("download");
            }
            if(!TextUtils.isEmpty(description)) {
                request.setDescription(description);
            } else {
                request.setTitle("download");
            }
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType(mimeType);

            String filePath = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//SD卡是否正常挂载
                filePath = FileUtils.getAbsolutePath(context) + File.separator + "download";
            } else {
                return;
            }

            String downloadFilePath = filePath + File.separator + fileName;
            // 若存在，则删除
//            deleteFile(downloadUpdateApkFilePath);
            Uri fileUri = Uri.parse("file://" + downloadFilePath);
            request.setDestinationUri(fileUri);
            long downloadId = downloadManager.enqueue(request);
            Toast.makeText(context, R.string.start_download, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
