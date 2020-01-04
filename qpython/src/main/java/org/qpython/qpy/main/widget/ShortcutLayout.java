package org.qpython.qpy.main.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.qpython.qpy.R;
import jackpal.androidterm.emulatorview.ShortcutBean;

/**
 * Created by Hmei
 * 11/20/17
 */
public class ShortcutLayout extends LinearLayout {
    EditText editText;
    Indent   indent;

    public void init(EditText editText, Indent indent) {
        this.editText = editText;
        this.indent = indent;
    }

    public ShortcutLayout(Context context) {
        super(context);
    }

    public ShortcutLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShortcutLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initShortcut();
    }

    private void initShortcut() {
//        shortcutLayout = (LinearLayout) binding.control.vsShortcut.getViewStub().inflate();
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        String[] shortcuts = getResources().getStringArray(R.array.term_shortcut);
        for (String shortcut : shortcuts) {
            ShortcutBean shortcutBean = new ShortcutBean(shortcut, true);
            switch (shortcutBean.getName()) {
                case "tab":
                    shortcutBean.setListener(v -> editText.getText().insert(editText.getSelectionStart(), getContext().getString(R.string.insert_tab)));

                    //shortcutBean.setListener(view -> indent.tabIndent());
                    break;
                case "del":
                    shortcutBean.setListener(view -> indent.delIndent());
                    break;
                case "ltab":
                    shortcutBean.setListener(v -> indent.leftIndent());
                    break;
                case "rtab":
                    shortcutBean.setListener(v -> indent.rightIndent());
                    break;
                case "def":
                    shortcutBean.setListener(v -> {
                        int startIndex = editText.getSelectionStart();
                        editText.getText().insert(startIndex, getContext().getString(R.string.insert_def));
                        editText.setSelection(startIndex + shortcutBean.getName().length() + 1);
                    });
                    break;
                case "if":
                    shortcutBean.setListener(v -> {
                        int startIndex = editText.getSelectionStart();
                        editText.getText().insert(startIndex, getContext().getString(R.string.insert_if));
                        editText.setSelection(startIndex + shortcutBean.getName().length() + 1);
                    });
                    break;
                case "for":
                    shortcutBean.setListener(v -> {
                        int startIndex = editText.getSelectionStart();
                        editText.getText().insert(startIndex, getContext().getString(R.string.insert_for));
                        editText.setSelection(startIndex + shortcutBean.getName().length() + 1);
                    });
                    break;
                case "imt":
                    shortcutBean.setListener(v -> {
                        final String IMPORT = "import ";
                        String text = editText.getText().toString();
                        if (text.contains(IMPORT)) {
                            int index = text.indexOf(IMPORT);
                            int lineEndIndex = text.indexOf("\n", index);
                            editText.getText().insert(lineEndIndex + 1, getContext().getString(R.string.insert_import));
                            editText.setSelection(lineEndIndex + 1 + IMPORT.length());
                        } else {
                            editText.getText().append(IMPORT);
                        }
                    });
                    break;
                case "imf":
                    shortcutBean.setListener(v -> {
                        final String FROM = "from ";
                        String text = editText.getText().toString();
                        if (text.contains(FROM)) {
                            int index = text.indexOf(FROM);
                            int lineEndIndex = text.indexOf("\n", index);
                            editText.getText().insert(lineEndIndex + 1, getContext().getString(R.string.insert_import_f));
                            editText.setSelection(lineEndIndex + 1 + FROM.length());
                        } else {
                            editText.getText().append(getContext().getString(R.string.insert_import_f));
                        }
                    });
                    break;
                case "clz":
                    shortcutBean.setListener(v -> {
                        int startIndex = editText.getSelectionStart();
                        editText.getText().insert(startIndex, getContext().getString(R.string.insert_clz));
                        editText.setSelection(startIndex + "class".length() + 1);
                    });
                    break;
                case "ef":
                    shortcutBean.setListener(v -> {
                        int startIndex = editText.getSelectionStart();
                        editText.getText().insert(startIndex, getContext().getString(R.string.insert_ef));
                        editText.setSelection(startIndex + "elif".length() + 1);
                    });
                    break;
                case "frm":
                    shortcutBean.setListener(v -> editText.getText().insert(editText.getSelectionStart(), getContext().getString(R.string.insert_frm)));
                    break;
                case "el":
                    shortcutBean.setListener(v -> editText.getText().insert(editText.getSelectionStart(), getContext().getString(R.string.insert_el)));
                    break;
                case "ret":
                    shortcutBean.setListener(v -> editText.getText().insert(editText.getSelectionStart(), getContext().getString(R.string.insert_ret)));
                    break;
                default:
                    shortcutBean.setListener(v -> editText.getText().insert(editText.getSelectionStart(), shortcutBean.getName()));
                    break;
            }

            switch (shortcutBean.getName()) {
                case "ltab":
                    ImageButton tab = (ImageButton) inflater.inflate(R.layout.widget_shortcut_imgbtn, null);
                    tab.setImageResource(R.drawable.tab1);
                    tab.setOnClickListener(shortcutBean.getListener());
                    addView(tab);
                    break;
                case "rtab":
                    ImageButton rtab = (ImageButton) inflater.inflate(R.layout.widget_shortcut_imgbtn, null);
                    rtab.setImageResource(R.drawable.tab2);
                    rtab.setOnClickListener(shortcutBean.getListener());
                    addView(rtab);
                    break;
                default:
                    Button button = (Button)inflater.inflate(R.layout.widget_shortcut_btn, null);
                    button.setText(shortcutBean.getName());
                    button.setOnClickListener(shortcutBean.getListener());
                    addView(button);
                    break;
            }
        }
    }
}
