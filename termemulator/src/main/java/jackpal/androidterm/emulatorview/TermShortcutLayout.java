package jackpal.androidterm.emulatorview;

import android.app.Instrumentation;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by Hmei
 * 11/20/17
 */
public class TermShortcutLayout extends LinearLayout {
    private EnterCallback callback;

    public TermShortcutLayout(Context context) {
        super(context);
    }

    public TermShortcutLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TermShortcutLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCallback(EnterCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initShortcut();
    }

    private void initShortcut() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String[] shortcuts1 = getResources().getStringArray(R.array.term_shortcut_head);
        String[] shortcuts2 = getResources().getStringArray(R.array.term_shortcut);
        ArrayList<String> shortcuts = new ArrayList<>();
        for (int i=0;i<shortcuts1.length;i++) {
            shortcuts.add(shortcuts1[i]);
        }
        for (int i=0;i<shortcuts2.length;i++) {
            shortcuts.add(shortcuts2[i]);
        }
        for (String shortcut : shortcuts) {
            final ShortcutBean shortcutBean = new ShortcutBean(shortcut, true);
            switch (shortcutBean.getName()) {
                case "tab":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.enter("[tab]");
                        }
                    });
                    break;
                case "del":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Instrumentation inst = new Instrumentation();
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DEL);
                                }
                            }).start();
                        }
                    });
                    break;
                case "ltab":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Instrumentation inst = new Instrumentation();
                                    for (int i = 0; i < 4; i++) {
                                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_DEL);
                                    }
                                }
                            }).start();
                        }
                    });
                    break;
                case "rtab":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.enter("    ");
                        }
                    });
                    break;
                case "def":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.enter("def ");
                        }
                    });
                    break;
                case "if":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.enter("if ");
                        }
                    });
                    break;
                case "for":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.enter("for ");
                        }
                    });
                    break;
                case "imt":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.enter("import ");
                        }
                    });
                    break;
                case "frm":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.enter("from ");
                        }
                    });
                    break;
                case "clz":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.enter("class ");
                        }
                    });
                    break;
                case "ef":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.enter("elif ");
                        }
                    });
                    break;
                case "el":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.enter("else ");
                        }
                    });
                    break;
                case "ret":
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.enter("return ");
                        }
                    });
                    break;
                default:
                    shortcutBean.setListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callback.enter(shortcutBean.getName());
                        }
                    });
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
                    Button button = (Button) inflater.inflate(R.layout.widget_shortcut_btn, null);
                    button.setText(shortcutBean.getName());
                    button.setOnClickListener(shortcutBean.getListener());
                    addView(button);
                    break;
            }
        }
    }

    public interface EnterCallback {
        void enter(String text);
    }
}
