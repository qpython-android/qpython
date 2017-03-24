package org.qpython.qpy.program;

import org.qpython.qpy.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProgramUtils {
    public static int scriptType(String path, boolean p) {
        String content;
        if (p) {
            content = getFileContents(path + "/main.py", 64);
        } else {
            content = getFileContents(path, 64);
        }
        boolean isQApp = content.contains("#qpy:qpyapp");
        boolean isCons = (!content.contains("#qpy:kivy") && !isQApp);
        boolean isWeb = content.contains("#qpy:webapp");
        boolean isKivy = content.contains("#qpy:kivy");

        if (isWeb) {
            return p ? R.drawable.ic_project_webapp : R.drawable.ic_script_webapp;
        } else if (isCons) {
            return p ? R.drawable.ic_project_console : R.drawable.ic_script_console;
        } else if (isQApp) {
            return p ? R.drawable.ic_project_qscript : R.drawable.ic_script_qscript;
        } else if (isKivy) {
            return p ? R.drawable.ic_project_kivyapp : R.drawable.ic_script_kivyapp;
        } else {
            return p ? R.drawable.ic_project_console : R.drawable.ic_script_console;
        }
    }

    private static String getFileContents(String path, int pos) {

        File scriptFile = new File(path);
        String tContent = "";
        if (scriptFile.exists()) {
            BufferedReader in;
            try {
                in = new BufferedReader(new FileReader(scriptFile));
                String line;

                while ((line = in.readLine()) != null) {
                    tContent += line + "\n";
                    if (tContent.length() >= pos) {
                        in.close();
                        return tContent;
                    }
                }
                in.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return tContent;
    }

    public static String getFileTxt(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line+"\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
