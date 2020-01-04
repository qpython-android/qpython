package org.qpython.qpy.main.adapter;

import org.greenrobot.eventbus.EventBus;
import org.qpython.qpy.R;
import org.qpython.qpy.databinding.ItemFolderLittleBinding;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.texteditor.EditorActivity;

public class EditorFileTreeAdapter extends FileTreeAdapter {
    public EditorFileTreeAdapter(String rootPath) {
        super(rootPath);

    }

    @Override
    void itemClick(ItemFolderLittleBinding binding, FileTreeAdapter.FileTreeBean folderBean) {
        String[] ext = App.getContext().getResources().getStringArray(R.array.text_ext);
        EditorActivity.OpenFileEvent openFileEvent = new EditorActivity.OpenFileEvent();
        for (String s : ext) {
            if (folderBean.name.endsWith(s)) {
                openFileEvent.filePath = folderBean.path;
            }
        }
        EventBus.getDefault().post(openFileEvent);
    }
}
