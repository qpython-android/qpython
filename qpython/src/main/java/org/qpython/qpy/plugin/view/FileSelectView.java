package org.qpython.qpy.plugin.view;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.qpython.qpy.R;


import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSelectView extends ListView implements AdapterView.OnItemClickListener {

    public final static String TAG = "FileSelectView";
    public static final String ON_ERROR_MSG = "No rights to access!";

    private static final String sRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String PARENT_DIR = "..";
    public static final String FOLDER = ".";
    public static final String EMPTY = "";

    private Deque<Integer> mTopStack = new ArrayDeque<>();
    private Deque<Integer> mPositionStack = new ArrayDeque<>();
    private Callback mCallback = null;
    private String mPath = sRoot;
    private List<Map<String, Object>> list = null;
    private Map<String, Integer> mIconMap = null;


    // 参数说明
    // context:上下文
    // mCallback:一个传递Bundle参数的回调接口
    // iconMap:用来根据后缀显示的图标资源ID。
    //  根目录图标的索引为sRoot;
    //  父目录的索引为sParent;
    //  文件夹的索引为sFolder;
    //  默认图标的索引为sEmpty;
    //  其他的直接根据后缀进行索引，比如.wav文件图标的索引为"wav"
    public FileSelectView(Context context, Callback callback, Map<String, Integer> iconMap) {
        super(context);

        mIconMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : iconMap.entrySet()) {
            mIconMap.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        mCallback = callback;
        setOnItemClickListener(this);
        refreshFileList();
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
        refreshFileList();
    }

    private String getSuffix(String filename) {
        int dix = filename.lastIndexOf('.');
        if (dix < 0) {
            return "";
        } else {
            return filename.substring(dix + 1);
        }
    }

    private int getImageId(String s) {
        if (mIconMap == null) {
            return 0;
        } else if (mIconMap.containsKey(s)) {
            return mIconMap.get(s);
        } else if (mIconMap.containsKey(EMPTY)) {
            return mIconMap.get(EMPTY);
        } else {
            return 0;
        }
    }

    public int refreshFileList() {
        // 刷新文件列表
        File[] files = null;
        try {
            files = new File(mPath).listFiles();
        } catch (Exception e) {
            files = null;
        }
        if (files == null) {
            // 访问出错
            Toast.makeText(getContext(), ON_ERROR_MSG, Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (list != null) {
            list.clear();
        } else {
            list = new ArrayList<>(files.length);
        }
        // 用来先保存文件夹和文件夹的两个列表
        ArrayList<Map<String, Object>> folderList = new ArrayList<>();
        ArrayList<Map<String, Object>> fileList = new ArrayList<>();

        // 顶级目录不显示上一级
        if (!this.mPath.equals(sRoot)) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", PARENT_DIR);
            map.put("mPath", mPath);
            map.put("img", getImageId(PARENT_DIR));
            list.add(map);
        }

        for (File file : files) {
            // 空文件夹不显示
            if (file.isDirectory() && !(file.listFiles().length == 0)) {
                // 添加有目录或含有指定文件类型的文件夹
                for (File subFile : file.listFiles()) {
                    if (subFile.isDirectory() || mIconMap.containsKey(getSuffix(subFile.getName()))) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", file.getName());
                        map.put("mPath", file.getPath());
                        map.put("img", getImageId(FOLDER));
                        folderList.add(map);
                        break;
                    }
                }
            } else if (file.isFile()) {
                // 添加文件
                String sf = getSuffix(file.getName()).toLowerCase();
                if (mIconMap.containsKey(sf.toLowerCase())) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", file.getName());
                    map.put("mPath", file.getPath());
                    map.put("img", getImageId(sf));
                    fileList.add(map);
                }
            }
        }

        //移除根目录
        list.addAll(folderList); // 先添加文件夹，确保文件夹显示在上面
        list.addAll(fileList);    //再添加文件

        SimpleAdapter adapter = new SimpleAdapter(getContext(), list, R.layout.list_file_item,
                new String[]{"img", "name", "mPath"},
                new int[]{R.id.file_dialog_item_img, R.id.file_dialog_item_name, R.id.file_dialog_item_path});
        this.setAdapter(adapter);
        return files.length;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        int lastPosition = 0;
        int lastTop = 0;

        // 条目选择  ?
        String pt = (String) list.get(position).get("mPath");
        String fn = (String) list.get(position).get("name");
        if (fn.equals(sRoot) || fn.equals(PARENT_DIR)) {
            // 如果是更目录或者上一层
            File fl = new File(pt);
            String ppt = fl.getParent();
            if (ppt != null) {
                // 返回上一层
                mPath = ppt;
                if (!mPositionStack.isEmpty() && !mTopStack.isEmpty()) {
                    lastPosition = mPositionStack.pop();
                    lastTop = mTopStack.pop();
                }
            } else {
                // 返回根目录
                mPath = sRoot;
                mPositionStack.clear();
                mTopStack.clear();
            }
        } else {
            File fl = new File(pt);
            if (fl.isFile()) {
                mCallback.onSelect(fl);
                return;
            } else if (fl.isDirectory()) {
                // 如果是文件夹
                // 那么进入选中的文件夹
                mPath = pt;
                mPositionStack.push(position);
                mTopStack.push(v.getTop());
            }
        }

        refreshFileList();
        setSelectionFromTop(lastPosition, lastTop);
    }

    public interface Callback {
        void onSelect(File file);
    }
}
