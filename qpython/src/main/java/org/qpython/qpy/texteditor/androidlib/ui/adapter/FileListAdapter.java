package org.qpython.qpy.texteditor.androidlib.ui.adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.qpython.qpy.R;
import org.qpython.qpy.texteditor.androidlib.common.UIUtils;
import org.qpython.qpy.texteditor.androidlib.data.FileUtils;

import java.io.File;
import java.util.List;


/**
 * A File List Adapter used to display folders and files
 */
public class FileListAdapter extends ArrayAdapter<File> {

    private int               mLayout;
    private boolean           mIconOnTop;
    private File              mFolder;
    private ThumbnailProvider mThumbnailProvider;
    private List<File>        mSelection;

    /**
     * Constructor
     *
     * @param context The current context
     * @param objects The objects to represent in the ListView.
     * @param folder  the parent folder of the items presented, or null if the top
     *                folder should not be displayed as up
     */
    public FileListAdapter(Context context, List<File> objects, File folder) {
        this(context, objects, folder, R.layout.item_file);
    }

    /**
     * Constructor
     *
     * @param context The current context
     * @param objects The objects to represent in the ListView.
     * @param folder  the parent folder of the items presented, or null if the top
     *                folder should not be displayed as up
     * @param layout  the layout to use
     */
    private FileListAdapter(Context context, List<File> objects, File folder,
                            int layout) {
        super(context, layout, objects);
        mLayout = layout;
        mFolder = folder;
    }

    private static int getIconForFile(File file) {
        if (file.isDirectory()) {
            return R.drawable.ic_editor_folder;
        } else {
            return R.drawable.ic_editor_file;
        }
    }

    /**
     * @see ArrayAdapter#getView(int, View, ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        File file;
        View v;
        TextView textView;
        String text;
        int icon, style;

        // recycle the view
        v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_file, null);
        }

        // get the file infos
        file = getItem(position);
        style = Typeface.BOLD;
        if (file != null) {
            text = file.getName();

//            if ((position == 0) && (mFolder != null)
//                    && (file.equals(mFolder.getParentFile()))
//                    && (!mFolder.getPath().equals("/"))) {
//                icon = R.drawable.up;
//                text = "";
//            } else {
            if (FileUtils.isSymLink(file)) {
                File target = FileUtils.getSymLinkTarget(file);
                if (target.equals(FileUtils.STORAGE)) {
                    icon = R.drawable.prev;
                } else if (target.isDirectory()) {
                    icon = R.drawable.ic_editor_folder_little;
                } else {
                    icon = R.drawable.ic_editor_file_little;
                }
            } else {
                if (position == 0) {
                    icon = R.drawable.prev;
                } else {
                    icon = getIconForFile(file);
                }
            }
//            }
        } else {
            text = "";
            icon = R.drawable.ic_editor_file_little;
        }

        Drawable thumbnail = null;
        Drawable selectedIcon = null;

        // File Icon / Thumbnail
        if (mThumbnailProvider != null) {
            thumbnail = mThumbnailProvider.getThumbnailForFile(getContext(),
                    file);
        }
        if (thumbnail == null) {
            thumbnail = getContext().getResources().getDrawable(icon);
        }

        int size = UIUtils.getPxFromDp(getContext(), 38);

        if (thumbnail != null) {
            double ratio = ((double) thumbnail.getIntrinsicWidth())
                    / ((double) thumbnail.getIntrinsicHeight());
            if (ratio > 1) {
                thumbnail.setBounds(0, 0, size, (int) (size / ratio));
            } else {
                thumbnail.setBounds(0, 0, (int) (size * ratio), size);
            }
        }

        // Handle selection
        int color = Color.LTGRAY;
        if ((mSelection != null) && (mSelection.contains(file))) {
            selectedIcon = getContext().getResources().getDrawable(
                    R.drawable.ic_program_install);
            selectedIcon.setBounds(0, 0, size, size);
            style = Typeface.BOLD_ITALIC;
            color = Color.rgb(0, 192, 0);
        }

        // Setup name and icon
        textView = (TextView) v.findViewById(R.id.textFileName);
        if (textView != null) {
            textView.setText(text);

            if (mIconOnTop) {
                textView.setCompoundDrawables(null, thumbnail, null, null);
                textView.setGravity(Gravity.CENTER);
            } else {
                textView.setCompoundDrawables(thumbnail, null, selectedIcon,
                        null);
                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            }

            textView.setTypeface(Typeface.DEFAULT, style);

            textView.setEllipsize(TruncateAt.MIDDLE);
            textView.setSingleLine();
            textView.setTextColor(color);
        }

        return v;
    }

    /**
     * @param folder the current parent folder for displayed files
     */
    public void setCurrentFolder(File folder) {
        mFolder = folder;
    }

    /**
     * @param iconOnTop let the icon be above the file name
     */
    public void setIconOnTop(boolean iconOnTop) {
        mIconOnTop = iconOnTop;
    }

    /**
     * @param provider the {@link ThumbnailProvider} for this adapter
     */
    public void setThumbnailProvider(ThumbnailProvider provider) {
        mThumbnailProvider = provider;
    }

    /**
     * @param selection the list of selected files
     */
    public void setSelection(List<File> selection) {
        mSelection = selection;
    }
}
