package org.qpython.qpysdk.utils;

import android.util.Log;

import com.quseit.common.jtar.TarEntry;
import com.quseit.common.jtar.TarInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;


public class FileExtract {
    public boolean extractTar(File tar, String target) {

        byte buf[] = new byte[8 * 1024];

        InputStream assetStream;
        TarInputStream tis;

        try {
            assetStream = new FileInputStream(tar);
            tis = new TarInputStream(new BufferedInputStream(new GZIPInputStream(new BufferedInputStream(assetStream, 8192)), 8192));
        } catch (IOException e) {
            return false;
        }

        while (true) {
            TarEntry entry;

            try {
                entry = tis.getNextEntry();
            } catch (java.io.IOException e) {
                return false;
            }

            if (entry == null) {
                Log.d("FileExtract","ENCOUNTER NULL WHEN extractTarS");
                break;
            }
            if (entry.isDirectory()) {

                try {
                    new File(target + "/" + entry.getName()).mkdirs();
                } catch (SecurityException ignore) {
                }
                continue;
            }

            OutputStream out = null;
            String path = target + "/" + entry.getName().trim();

            try {
                Log.d("FileExtract","FILE:"+path);
                out = new BufferedOutputStream(new FileOutputStream(path), 8192);
            } catch (FileNotFoundException ignore) {
            } catch (SecurityException ignore) {
            }

            if (out == null) {
                return false;
            }

            try {
                while (true) {
                    int len = tis.read(buf);

                    if (len == -1) {
                        break;
                    }

                    out.write(buf, 0, len);
                }

                out.flush();
                out.close();
            } catch (java.io.IOException e) {
                return false;
            }
        }

        try {
            tis.close();
            assetStream.close();
        } catch (IOException ignore) {
        }

        return true;
    }
}
