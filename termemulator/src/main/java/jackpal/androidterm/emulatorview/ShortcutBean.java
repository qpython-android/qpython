package jackpal.androidterm.emulatorview;

import android.view.View;

import java.io.Serializable;

/**
 * Created by Hmei
 * 11/15/17
 */
public class ShortcutBean implements Serializable{
    private String name;
    private boolean isUnDeletable;
    private View.OnClickListener listener;

    public ShortcutBean(String name, boolean isUnDeletable) {
        this.name = name;
        this.isUnDeletable = isUnDeletable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUnDeletable() {
        return isUnDeletable;
    }

    public void setUnDeletable(boolean unDeletable) {
        isUnDeletable = unDeletable;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
