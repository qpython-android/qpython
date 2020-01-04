package org.qpython.qpy.texteditor;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Toast;

import com.quseit.util.NAction;
import com.quseit.util.NStorage;

import org.markdown4j.Markdown4jProcessor;
import org.qpython.qpy.R;
import org.qpython.qpy.console.ScriptExec;
import org.qpython.qpy.databinding.LayoutEditorBinding;
import org.qpython.qpy.databinding.SearchTopBinding;
import org.qpython.qpy.main.activity.GistEditActivity;
import org.qpython.qpy.main.activity.QWebViewActivity;
import org.qpython.qpy.main.activity.SignInActivity;
import org.qpython.qpy.main.app.App;
import org.qpython.qpy.main.widget.Indent;
import org.qpython.qpy.main.widget.ShortcutLayout;
import org.qpython.qpy.texteditor.common.Constants;
import org.qpython.qpy.texteditor.common.RecentFiles;
import org.qpython.qpy.texteditor.common.Settings;
import org.qpython.qpy.texteditor.ui.view.EnterDialog;
import org.qpython.qpy.texteditor.undo.TextChangeWatcher;
import org.qpython.qpy.texteditor.widget.crouton.Crouton;
import org.qpython.qpy.texteditor.widget.crouton.Style;
import org.qpython.qpysdk.QPyConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static android.content.Context.MODE_PRIVATE;
import static org.qpython.qpy.texteditor.androidlib.data.FileUtils.deleteItem;
import static org.qpython.qpy.texteditor.androidlib.data.FileUtils.renameItem;
import static org.qpython.qpy.texteditor.androidlib.ui.Toaster.showToast;

/**
 * @author River
 */

public class TedFragment extends Fragment implements Constants, TextWatcher, Indent {
    public static final String TAG                = "TED";
    public static final int    LOGIN_REQUEST_CODE = 5106;
    public static final int    OPEN_REQUEST_CODE  = 5233;


    private static final String Apache_License  = "Apache_License";
    private static final String The_MIT_License = "The_MIT_License";

    final int DOC_FLAG = 10001;

    protected boolean mInUndo;
    protected boolean mDoNotBackup;

    protected TextChangeWatcher mWatcher = new TextChangeWatcher();
    private EditorActivity activity;

    private LayoutEditorBinding binding;
    private SearchTopBinding    searchTopBinding;
    private ShortcutLayout      shortcutLayout;

    public static TedFragment newInstance(String content) {
        TedFragment myFragment = new TedFragment();

        Bundle args = new Bundle();
        args.putString(TAG, content);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_editor, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = DataBindingUtil.bind(view);
        if (activity.mReadOnly) {
            binding.editor.setEnabled(false);
        }
        initListener();
        initContent();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (EditorActivity) context;
        //此处view未进行初始化不应该对view进行操作
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) {
            binding.editor.updateFromSettings("");

            if (Settings.SHOW_GIST) {
                binding.share.setVisibility(View.VISIBLE);

            } else {
                binding.share.setVisibility(View.GONE);

            }
        }
    }

    private void initListener() {
        binding.ibMore.setOnClickListener(view -> {
            if (view.isSelected()) {
                shortcutLayout.setVisibility(View.GONE);
                binding.control.llControl.setVisibility(View.VISIBLE);
                binding.ibMore.setImageResource(R.drawable.ic_editcode);
            } else {
                if (!binding.control.vsShortcut.isInflated()) {
                    shortcutLayout = (ShortcutLayout) binding.control.vsShortcut.getViewStub().inflate();
                    shortcutLayout.init(binding.editor,this);
                }
                if (binding.control.ibLock.isSelected()) {
                    binding.control.ibLock.setSelected(false);
                    binding.control.ibLock.setImageResource(R.drawable.ic_editor_keyboard);
                    binding.editor.setEnabled(true);
                }
                shortcutLayout.setVisibility(View.VISIBLE);
                binding.control.llControl.setVisibility(View.GONE);
                binding.ibMore.setImageResource(R.drawable.ic_edittext);
            }
            view.setSelected(!view.isSelected());
        });
        binding.control.ibSave.setOnClickListener(v -> ((EditorActivity) getActivity()).saveContent(true, false));
        binding.control.ibSaveAs.setOnClickListener(v -> ((EditorActivity) getActivity()).saveAs());


        binding.control.ibSave.setOnLongClickListener(v -> {
            TedLocalActivity.start(getActivity(), TedLocalActivity.REQUEST_SAVE_AS, REQUEST_SAVE_AS,"");
            return true;
        });
        binding.control.ibSnippet.setOnClickListener(v -> onSnippets());
        binding.control.ibRecent.setOnClickListener(v -> {
            TedLocalActivity.start(getActivity(), TedLocalActivity.REQUEST_RECENT, OPEN_REQUEST_CODE,"");
            binding.ibMore.setImageResource(R.drawable.ic_editor_more_horiz);
            binding.ibMore.setSelected(false);
        });
        binding.control.ibSave.setOnClickListener(v -> ((EditorActivity) getActivity()).saveContent(true, false));
        binding.control.ibJumpTo.setOnClickListener(v -> goToLine());
        binding.control.playBtn.setOnClickListener(view -> {
            if (activity.mDirty) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.warning)
                        .setMessage(R.string.run_dirty_hint)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            ((EditorActivity) getActivity()).saveContent(false, false);
                            runFile();
                        })
                        .setNegativeButton(R.string.no_im_good, ((dialog, which) -> runFile()))
                        .show();
            } else {
                runFile();
            }
        });
        binding.control.ibSearch.setOnClickListener(view -> {
            if (!binding.vsSearchTop.isInflated()) {
                searchTopBinding = DataBindingUtil.bind(binding.vsSearchTop.getViewStub().inflate());
                initSearchBarListener();
                setSearchState();
            }
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                startAnim();
            }
            binding.searchBottom.rlSearchBottom.setVisibility(View.VISIBLE);
            searchTopBinding.llSearch.setVisibility(View.VISIBLE);
            searchTopBinding.textSearch.requestFocus();
        });
        binding.control.ibUndo.setOnClickListener(v -> undo());
        binding.control.ibRedo.setOnClickListener(v -> redo());
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener((EditorActivity) getActivity());

        if (Settings.SHOW_GIST) {
            binding.share.setOnClickListener(v -> {
                if (activity.mCurrentFilePath == null) {
                    Toast.makeText(getContext(), R.string.share_empty_hint, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (activity.mDirty) {
                    ((EditorActivity) getActivity()).promptSaveDirty();
                    return;
                }
                if (App.getUser() != null) {
                    GistEditActivity.start(getContext(), activity.mCurrentFilePath);
                } else {
                    Intent intent = new Intent(getContext(), SignInActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST_CODE);
                }
            });
        } else {
            binding.share.setVisibility(View.GONE);
        }

        binding.control.ibLock.setOnClickListener(v -> {
            if (v.isSelected()) {
                binding.control.ibLock.setImageResource(R.drawable.ic_editor_keyboard);
                binding.editor.setEnabled(true);
            } else {
                binding.control.ibLock.setImageResource(R.drawable.ic_editor_keyboardlock);
                binding.editor.setEnabled(false);
            }

            v.setSelected(!v.isSelected());
        });
    }

    private void initContent() {
        binding.editor.setText(getArguments().getString(TAG));
        binding.editor.setEnabled(!activity.mReadOnly);
        binding.editor.addTextChangedListener(this);
        binding.editor.setOnKeyListener(((v, keyCode, event) -> {
            if (KeyEvent.ACTION_UP == event.getAction()
                    && (KeyEvent.KEYCODE_NUMPAD_ENTER == event.getKeyCode()
                    || KeyEvent.KEYCODE_ENTER == event.getKeyCode())) {
                activity.saveContent(false, true);
            }
            return false;
        }));
        updateType();
    }

    private void runFile() {
        if (activity.mCurrentFilePath != null
                && (activity.mCurrentFilePath.endsWith(".py") || activity.mCurrentFilePath.endsWith(".md")
                || activity.mCurrentFilePath.endsWith(".html") || activity.mCurrentFilePath.endsWith(".htm")
                || activity.mCurrentFilePath.endsWith(".lua") || activity.mCurrentFilePath.endsWith(".sh"))) {
            if (activity.mCurrentFileName.endsWith(".html")) {
                String content = binding.editor.getText().toString();
                QWebViewActivity.loadHtml(getContext(), content, activity.mCurrentFileName);
            } else if (activity.mCurrentFileName.endsWith(".md")) {
                String content = binding.editor.getText().toString();
                try {
                    String markdown = new Markdown4jProcessor().process(content);
                    QWebViewActivity.loadHtml(getContext(), markdown, activity.mCurrentFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    Crouton.showText(getActivity(), getResources().getString(R.string.convert_fail), Style.ALERT);
                }
            } else {
                if (activity.mCurrentFilePath.contains("/projects")) {
                    ScriptExec.getInstance().playProject(getActivity(), activity.mCurrentFilePath.replace("/main.py", ""), false);
                } else {
                    ScriptExec.getInstance().playScript(getActivity(), activity.mCurrentFilePath, null, false);
                }
            }
        } else {
            Toast.makeText(getContext(), R.string.qedit_not_support, Toast.LENGTH_SHORT).show();
        }
    }

    protected void updateType() {
        if (activity.mCurrentFileName != null) {
            String[] syntaxExts = getResources().getStringArray(R.array.syntax_ext);
            for (String syntaxExt : syntaxExts) {
                if (activity.mCurrentFileName.endsWith(syntaxExt)) {
                    binding.editor.updateFromSettings(syntaxExt);
                }
            }

            if (activity.mCurrentFileName.endsWith(".py") || activity.mCurrentFileName.endsWith(".sh")) {
                binding.control.playBtn.setImageResource(R.drawable.ic_editor_run);
            } else if (activity.mCurrentFileName.endsWith(".md")
                    || activity.mCurrentFileName.endsWith(".html") || activity.mCurrentFileName.endsWith(".htm")) {
                binding.control.playBtn.setImageResource(R.drawable.ic_editor_web);
            }
        }
    }

    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    private void startAnim() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        Animator anim = ViewAnimationUtils.createCircularReveal(searchTopBinding.getRoot(), width, 0, 0, width);
        anim.start();
    }

    private void initSearchBarListener() {
        searchTopBinding.ibClear.setOnClickListener(v -> searchTopBinding.textSearch.setText(""));
        searchTopBinding.ibClose.setOnClickListener(v -> {
            searchTopBinding.llSearch.setVisibility(View.GONE);
            binding.searchBottom.rlSearchBottom.setVisibility(View.GONE);
        });

        binding.searchBottom.ivSearch.setOnClickListener(v -> searchNext());
        binding.searchBottom.buttonSearchNext.setOnClickListener(v -> searchNext());

        binding.searchBottom.buttonSearchPrev.setOnClickListener(v -> searchPrevious());
    }

    /**
     * Create a list of snippet
     */
    public void SnippetsList() {
        boolean isQpy3 = NAction.isQPy3(getContext());
        List<String> listItems = new ArrayList<>();
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + QPyConstants.BASE_PATH;
        String path = baseDir + (isQpy3 ? "/snippets3/" : "/snippets/");
        String files;
        File folder = new File(path);
        if (folder.exists()) {
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                for (File listOfFile : listOfFiles) {
                    if (listOfFile.isFile()) {
                        files = listOfFile.getName();
                        listItems.add(files);
                    }
                }
            }
        }

        final CharSequence colors[] = listItems.toArray(new CharSequence[listItems.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialog);
        builder.setTitle(R.string.info_snippets);

        builder.setItems(colors, (dialog, which) -> {
            try {
                insertSnippet("" + colors[which]);
            } catch (IOException e) {
                Toast.makeText(getContext().getApplicationContext(), R.string.fail_to_insert, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        builder.show();
    }

    /**
     * @param snippetName Apache_License/The_MIT_License/WEB_PROJECT/CONSOLE_PROJECT/KIVY_PROJECT
     */
    public void insertSnippet(String snippetName) throws IOException {
        boolean isQPy3 = NAction.isQPy3(getContext());
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + QPyConstants.BASE_PATH;
        String path = baseDir + (isQPy3 ? "/snippets3/" : "/snippets/");
        String s;
        switch (snippetName) {
            case Apache_License:
                s = readFile(path + Apache_License);
                break;
            case The_MIT_License:
                s = readFile(path + The_MIT_License);
                break;
            case QPyConstants.WEB_PROJECT:
                s = readFile(path + "QPy_WebApp");
                break;
            case QPyConstants.CONSOLE_PROJECT:
                s = readFile(path + "QPy_ConsoleApp");
                break;
            case QPyConstants.KIVY_PROJECT:
                s = readFile(path + "QPy_KivyApp");
                break;
            case QPyConstants.PYGAME_PROJECT:
                s = readFile(path + "QPy_PygameApp");
                break;
            case QPyConstants.QUIET_PROJECT:
                s = readFile(path + "QPy_QuietApp");
                break;
            default:
                s = readFile(path + "QPy_WebApp");
        }
        binding.editor.getText().insert(0, s);
    }

    public void saveCodeSnippet(String selectText) {
        boolean isQPy3 = NAction.isQPy3(getContext());
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + QPyConstants.BASE_PATH;
        String path = baseDir + (isQPy3 ? "/snippets3/" : "/snippets/");

        new EnterDialog(getContext())
                .setTitle(getString(R.string.save_as_snippets))
                .setHint(getString(R.string.enter_snippet))
                .setConfirmListener(name -> {
                    String dir = path + name;
                    File f = new File(dir);
                    if (f.exists()) {
                        Toast.makeText(getContext(), R.string.snippet_exist, Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        writeToFile(name, selectText);
                        Toast.makeText(getContext().getApplicationContext(), getString(R.string.save_as_snippets_1) + name,
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }
                })
                .show();
    }

    public void setSearch() {
        setSearchState();

        int startSelection = binding.editor.getSelectionStart();
        int endSelection = binding.editor.getSelectionEnd();
        String selectedText = binding.editor.getText().toString().substring(startSelection, endSelection);
        if (selectedText.length() != 0) {
            searchTopBinding.textSearch.setText(selectedText);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (Settings.UNDO && (!mInUndo) && (mWatcher != null))
            mWatcher.beforeChange(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mInUndo)
            return;

        if (Settings.UNDO && (!mInUndo) && (mWatcher != null))
            mWatcher.afterChange(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!activity.mDirty) {
            activity.mDirty = true;
            ((EditorActivity) getActivity()).updateTitle();
        }
    }

    public boolean setEditorText(String text) {
        if (text != null) {
            //view未初始化调用该方法导致空指针
            if (binding == null){
                Bundle args = new Bundle();
                args.putString(TAG, text);
                setArguments(args);
                return false;
            }
            binding.editor.removeTextChangedListener(this);
            mInUndo = true;
            binding.editor.setText(text);
            binding.editor.setOnKeyListener(null);
            binding.editor.setEnabled(!activity.mReadOnly);
            mWatcher = new TextChangeWatcher();
            if (activity.mCurrentFilePath != null) {
                RecentFiles.updateRecentList(activity.mCurrentFilePath);
                RecentFiles.saveRecentList(getContext().getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE));
            }
            activity.mDirty = false;
            mInUndo = false;
            mDoNotBackup = false;
            NStorage.setSP(getContext().getApplicationContext(), "qedit.last_filename", activity.mCurrentFilePath);
            updateType();
            binding.editor.addTextChangedListener(this);
            binding.editor.setOnKeyListener(((v, keyCode, event) -> {
                if (KeyEvent.ACTION_UP == event.getAction() && (KeyEvent.KEYCODE_NUMPAD_ENTER == event.getKeyCode()
                        || KeyEvent.KEYCODE_ENTER == event.getKeyCode())) {

                    activity.saveContent(false, true);
                }
                return false;
            }));
            return true;
        } else {
            Toast.makeText(getContext(), R.string.toast_open_error, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Undo the last change
     *
     * @return if an undo was don
     */
    protected boolean undo() {
        boolean didUndo = false;
        mInUndo = true;
        int caret;
        caret = mWatcher.undo(binding.editor.getText());
        if (caret >= 0) {
            binding.editor.setSelection(caret, caret);
            didUndo = true;
        }
        mInUndo = false;

        return didUndo;
    }

    protected boolean redo() {
        boolean didRedo = false;
        int caret;
        caret = mWatcher.redo(binding.editor.getText());
        if (caret >= 0) {
            binding.editor.setSelection(caret);
            didRedo = true;
        }
        return didRedo;
    }

    protected boolean isSearchShowing() {
        return searchTopBinding != null && searchTopBinding.llSearch.getVisibility() == View.VISIBLE;
    }

    protected String getEditorString() {
        return binding.editor.getText().toString();
    }

    public void onSnippets() {
        int startSelection = binding.editor.getSelectionStart();
        int endSelection = binding.editor.getSelectionEnd();
        String selectedText = binding.editor.getText().toString().substring(startSelection, endSelection);
        /**
         * Detect if the text is selected
         */
        if (selectedText.length() != 0) {
            saveCodeSnippet(selectedText);
        } else {
            SnippetsList();
        }
    }

    /**
     * Opens / close the setSearchState interface
     */
    protected void setSearchState() {
        if (searchTopBinding.llSearch!=null) {
            switch (searchTopBinding.llSearch.getVisibility()) {
                case View.GONE:
                    binding.returnBarBox.setVisibility(View.GONE);
                    searchTopBinding.llSearch.setVisibility(View.VISIBLE);
                    binding.searchBottom.rlSearchBottom.setVisibility(View.VISIBLE);
                    searchTopBinding.textSearch.requestFocus();
                    break;
                case View.INVISIBLE:
                    break;
                case View.VISIBLE:
                default:
                    searchTopBinding.llSearch.setVisibility(View.GONE);
                    binding.searchBottom.rlSearchBottom.setVisibility(View.GONE);
                    binding.returnBarBox.setVisibility(View.VISIBLE);
                    break;
            }
        }
        binding.editor.setEnabled(true);
    }

    /**
     * Uses the user input to setSearchState a file
     */
    @SuppressLint("DefaultLocale")
    protected void searchNext() {
        String search, text;
        int selection, next;

        search = searchTopBinding.textSearch.getText().toString();
        text = binding.editor.getText().toString();
        selection = binding.editor.getSelectionEnd();

        if (!Settings.SEARCHMATCHCASE) {
            search = search.toLowerCase();
            text = text.toLowerCase();
        }

        next = text.indexOf(search, selection);
        if (next > -1) {
            binding.editor.setSelection(next, next + search.length());
            if (!binding.editor.isFocused())
                binding.editor.requestFocus();
        } else {
            if (Settings.SEARCHWRAP) {
                next = text.indexOf(search);
                if (next > -1) {
                    binding.editor.setSelection(next, next + search.length());
                    if (!binding.editor.isFocused())
                        binding.editor.requestFocus();
                } else {
                    Crouton.showText(getActivity(), R.string.toast_search_not_found, Style.ALERT);
                }
            } else {
                Crouton.showText(getActivity(), R.string.toast_search_eof, Style.ALERT);
            }
        }
    }

    /**
     * Uses the user input to setSearchState a file
     */
    @SuppressLint("DefaultLocale")
    protected void searchPrevious() {
        String search, text;
        int selection, next;

        search = searchTopBinding.textSearch.getText().toString();
        text = binding.editor.getText().toString();
        selection = binding.editor.getSelectionStart() - 1;

        if (search.length() == 0) {
            Crouton.showText(getActivity(), R.string.toast_search_no_input, Style.ALERT);
            return;
        }

        if (!Settings.SEARCHMATCHCASE) {
            search = search.toLowerCase();
            text = text.toLowerCase();
        }

        next = text.lastIndexOf(search, selection);

        if (next > -1) {
            binding.editor.setSelection(next, next + search.length());
            if (!binding.editor.isFocused())
                binding.editor.requestFocus();
        } else {
            if (Settings.SEARCHWRAP) {
                next = text.lastIndexOf(search);
                if (next > -1) {
                    binding.editor.setSelection(next, next + search.length());
                    if (!binding.editor.isFocused())
                        binding.editor.requestFocus();
                } else {
                    Crouton.showText(getActivity(), R.string.toast_search_not_found, Style.INFO);
                }
            } else {
                Crouton.showText(getActivity(), R.string.toast_search_eof, Style.INFO);
            }
        }
    }

    /**
     * Indent the text left
     */
    @Override
    public void leftIndent() {
        int index = binding.editor.getSelectionStart();
        Editable editable = binding.editor.getText();
        if (index >= 4) {
            if (editable.subSequence(index - 4, index).toString().equals("    ")) {
                editable.delete(index - 4, index);
            }
        }
    }


    // same as rightIndent
    public void tabIndent() {
        int startSelection = binding.editor.getSelectionStart();
        int endSelection = binding.editor.getSelectionEnd();
        String selectedText = binding.editor.getText().toString().substring(startSelection, endSelection);
        Editable editable = binding.editor.getText();
        if (selectedText.length() != 0) {
            String startData = binding.editor.getText().toString();
            String textData = startData.substring(0, startSelection);
            if (textData.contains("\n")) {
                int newLineIndex = textData.lastIndexOf("\n");
                editable.replace(newLineIndex, newLineIndex, "\n\t");
            } else {
                editable.insert(0, "\t");
            }
            String indentedText = selectedText.replace("\n", "\n\t");
            editable.replace(startSelection, endSelection, indentedText);
        } else {
            int index = binding.editor.getSelectionStart();
            editable.insert(index, "\t");
        }

        binding.editor.setFocusable(true);
        binding.editor.setFocusableInTouchMode(true);
        binding.editor.requestFocus();
        binding.editor.requestFocusFromTouch();
        binding.editor.findFocus();
    }
    //same as leftIndentss
    public void delIndent() {
        int index = binding.editor.getSelectionStart();
        Editable editable = binding.editor.getText();
        if (index >= 1) {
            if (editable.subSequence(index - 1, index).toString().equals("    ")) {
                editable.delete(index - 1, index);
            }
        }
    }
    /**
     *
     * Indent the text right
     */
    @Override
    public void rightIndent() {
        int startSelection = binding.editor.getSelectionStart();
        int endSelection = binding.editor.getSelectionEnd();
        String selectedText = binding.editor.getText().toString().substring(startSelection, endSelection);
        Editable editable = binding.editor.getText();
        if (selectedText.length() != 0) {
            String startData = binding.editor.getText().toString();
            String textData = startData.substring(0, startSelection);
            if (textData.contains("\n")) {
                int newLineIndex = textData.lastIndexOf("\n");
                editable.replace(newLineIndex, newLineIndex, "\n    ");
            } else {
                editable.insert(0, "    ");
            }
            String indentedText = selectedText.replace("\n", "\n    ");
            editable.replace(startSelection, endSelection, indentedText);
        } else {
            int index = binding.editor.getSelectionStart();
            editable.insert(index, "    ");
        }

        binding.editor.setFocusable(true);
        binding.editor.setFocusableInTouchMode(true);
        binding.editor.requestFocus();
        binding.editor.requestFocusFromTouch();
        binding.editor.findFocus();
    }

    /**
     * Calculate the index of the Nth new PATTERN_LINE
     *
     * @param indexNewLine New PATTERN_LINE to find
     * @return Gives the position of the Nth new PATTERN_LINE
     */
    public int NewLineIndex(int indexNewLine) {
        String data = binding.editor.getText().toString();
        final StringBuffer sb = new StringBuffer(data);
        List<Integer> myList = new ArrayList<Integer>();
        myList.add(0);
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '\n')
                myList.add(i + 1);
        }
        return myList.get(indexNewLine - 1);
    }

    public void goToLine() {
        new EnterDialog(getContext()).setTitle(getString(R.string.jump_to))
                .setEnterType(InputType.TYPE_CLASS_NUMBER)
                .setHint("Line : 1 - " + binding.editor.getLineCount())
                .setConfirmListener(content -> {
                    int line;
                    try {
                        line = Integer.parseInt(content);
                        if (line > binding.editor.getLineCount()) {
                            Toast.makeText(getContext(), R.string.fail_to_goto, Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        int position = NewLineIndex(line);
                        binding.editor.setSelection(position);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), R.string.fail_to_goto, Toast.LENGTH_SHORT).show();

                    }
                    return true;
                })
                .show();
    }

    private String readFile(String pathname) throws IOException {

        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int) file.length());
        String lineSeparator = System.getProperty("PATTERN_LINE.separator");
        if (lineSeparator==null) {
            lineSeparator = "\n";
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine()).append(lineSeparator);
            }
            return fileContents.toString();
        }
    }

    public void writeToFile(String filePath, String data) {
        try {
            FileOutputStream fOut = new FileOutputStream(filePath);
            fOut.write(data.getBytes());
            fOut.flush();
            fOut.close();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }
}
