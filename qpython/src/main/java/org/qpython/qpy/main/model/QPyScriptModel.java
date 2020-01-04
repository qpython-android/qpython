package org.qpython.qpy.main.model;

import android.graphics.drawable.Drawable;

import com.quseit.util.FileHelper;

import org.qpython.qpy.R;
import org.qpython.qpy.main.app.App;

import java.io.File;

/**
 * QPython script list item model
 * Created by Hmei on 2017-05-25.
 */

public class QPyScriptModel extends AppModel {
    private File    file;
    private boolean isProj;
    private int res = -1;

    public QPyScriptModel(File file) {
        this.file = file;
    }


    private int initRes() {
        isProj = getPath().contains("/projects");
        String content;
        if (isProj) {
            File mainPy = FileHelper.getMainFileByType(getFile());
            if (mainPy == null) {
                res = R.drawable.ic_project_qapp;
                return res;
            }
            content = org.qpython.qpysdk.utils.FileHelper.getFileContents(mainPy.getAbsolutePath());
        } else {
            content = org.qpython.qpysdk.utils.FileHelper.getFileContents(getPath());
        }
        boolean isWeb = content.contains("#qpy:webapp");
        boolean isQApp = content.contains("#qpy:quiet") || content.contains("#qpy:qpysrv");
        boolean isKivy = content.contains("#qpy:kivy");
        boolean isGame = content.contains("#qpy:pygame");
        boolean isPy3 = content.contains("#qpy:3");

        if (isWeb) {
            if (isPy3) {
                res = (isProj ? R.drawable.ic_project_webapp3 : R.drawable.ic_pyfile_webapp3);
            } else {
                res = (isProj ? R.drawable.ic_project_webapp : R.drawable.ic_pyfile_webapp);
            }
        } else if (isKivy || isGame) {
            if (isPy3) {
                res = (isProj ? R.drawable.ic_project_kivy3 : R.drawable.ic_pyfile_kivy3);
            } else {
                res = (isProj ? R.drawable.ic_project_kivy : R.drawable.ic_pyfile_kivy);
            }
        } else {
            if (isPy3) {
                res = (isProj ? R.drawable.ic_project_qapp3 : R.drawable.ic_pyfile_qapp3);
            } else {
                res = (isProj ? R.drawable.ic_project_qapp : R.drawable.ic_pyfile_qapp);
            }
        }
        return res;
    }

    @Override
    public Drawable getIcon() {
        return App.getContext().getResources().getDrawable(res == -1 ? initRes() : res);
    }

    @Override
    public String getLabel() {
        return file.getName();
    }

    @Override
    public int getIconRes() {
        return res == -1 ? initRes() : res;
    }

    public String getPath() {
        return file.getAbsolutePath();
    }

    public File getFile() {
        return file;
    }

    public boolean isProj() {
        return isProj;
    }
}
