package org.qpython.qpy.texteditor.common;

import java.io.File;

import android.os.Environment;

/**
 * @author x.gouchet
 */
public interface Constants {

    /**
     * Tag for the log ( = {@value} )
     */
     String TAG = "TED";

     String ACTION_WIDGET_OPEN = "org.qpython.qpy.texteditor.ACTION_TED_WIDGET_OPEN";

    String ACTION_WIDGET_TEXT = "org.qpython.qpy.texteditor.ACTION_TED_WIDGET_TEXT";

    /**
     * Fonts folder
     */
     String FONT_FOLDER_NAME = "fonts";
    /**
     * Font file
     */
     String FONT_FILE_NAME   = "ted_font.ttf";

    /**
     * clears the current text ( = {@value} )
     */
     int MENU_ID_NEW         = 0;
    /**
     * Saves the file ( = {@value} )
     */
     int MENU_ID_SAVE        = 1;
    /**
     * Savesas a new file ( = {@value} )
     */
     int MENU_ID_SAVE_AS     = 2;
    /**
     * open an existing file
     */
     int MENU_ID_OPEN        = 3;
    /**
     * open an existing file ( = {@value} )
     */
     int MENU_ID_OPEN_RECENT = 4;
    /**
     * open the settings ( = {@value} )
     */
     int MENU_ID_SETTINGS    = 5;
    /**
     * open the about page ( = {@value} )
     */
     int MENU_ID_ABOUT       = 6;
    /**
     * Search a string in the file ( = {@value} )
     */
     int MENU_ID_SEARCH      = 7;
    /**
     * undo last change ( = {@value} )
     */
     int MENU_ID_UNDO        = 8;
    /**
     * redo last change ( = {@value} )
     */
     int MENU_ID_REDO        = 9;
    /**
     * Quit the app ( = {@value} )
     */
     int MENU_ID_QUIT        = 666;

    /**
     * File of the external storage data
     */
     File   STORAGE          = Environment
            .getExternalStorageDirectory();
    /**
     * Path to the external storage data
     */
     String STORAGE_PATH     = STORAGE.getAbsolutePath();
    /**
     * name of the backup file
     */
     String BACKUP_FILE_NAME = "temp.bak";

    String PREFERENCES_GIST                     = "show_gist";

    /**
     * name of the shared preferences for this app ( = {@value} )
     */
     String PREFERENCES_NAME                     = "org.qpython.qpy.texteditor";
    /**
     * Preference tag to retrieve the recent files ( = {@value} )
     */
     String PREFERENCE_RECENTS                   = "recent_files";
    /**
     * Preference tag to retrieve the recent files ( = {@value} )
     */
     String PREFERENCE_MAX_RECENTS               = "max_recent_files";
     String PREFERENCE_MAX_LINES_NUM_WITH_SYNTAX = "max_lines_num_with_syntax";

    /**
     * Preference tag to retrieve the show PATTERN_LINE number ( = {@value} )
     */
     String PREFERENCE_SHOW_LINE_NUMBERS   = "show_line_numbers";
    /**
     * Preference tag to retrieve the wordwrap ( = {@value} )
     */
     String PREFERENCE_WORDWRAP            = "auto_break_lines";
    /**
     * Preference tag to retrieve the searchwrap ( = {@value} )
     */
     String PREFERENCE_SEARCHWRAP          = "search_wrap";
    /**
     * Preference tag to retrieve the setSearchState match case ( = {@value} )
     */
     String PREFERENCE_SEARCH_MATCH_CASE   = "search_match_case";
    /**
     * Preference tag to retrieve the Syntax Highlight ( = {@value} )
     */
     String PREFERENCE_HIGHLIGHT           = "highlight_syntax";
    /**
     * Preference tag to retrieve the Text Size ( = {@value} )
     */
     String PREFERENCE_TEXT_SIZE           = "text_size";
    /**
     * Preference tag to retrieve the End of lines pref ( = {@value} )
     */
     String PREFERENCE_END_OF_LINES        = "end_of_lines";
    /**
     * Preference tag to retrieve the Encoding pref ( = {@value} )
     */
     String PREFERENCE_ENCODING            = "encoding";
    /**
     * Preference tag to retrieve the Auto mode ( = {@value} )
     */
     String PREFERENCE_AUTO_SAVE           = "force_auto_save";
    /**
     * Preference tag to retrieve the Auto mode ( = {@value} )
     */
     String PREFERENCE_AUTO_SAVE_OVERWRITE = "auto_save_overwrite";
    /**
     * Preference tag to retrieve the Color Theme ( = {@value} )
     */
     String PREFERENCE_COLOR_THEME         = "color_theme";
    /**
     * Preference tag to retrieve the fling to scroll ( = {@value} )
     */
     String PREFERENCE_FLING_TO_SCROLL     = "fling_to_scroll";
    /**
     * Preference tag to retrieve if undo history is allowed ( = {@value} )
     */
     String PREFERENCE_ALLOW_UNDO          = "allow_undo";
    /**
     * Preference tag to retrieve the max undo stack ( = {@value} )
     */
     String PREFERENCE_MAX_UNDO_STACK      = "max_undo_stack";
    /**
     * Preference tag to retrieve the back button as undo ( = {@value} )
     */
     String PREFERENCE_BACK_BUTTON_AS_UNDO = "back_button_as_undo";
    /**
     * Preference tag to retrieve the use home page ( = {@value} )
     */
     String PREFERENCE_USE_HOME_PAGE       = "use_home_page";
    /**
     * Preference tag to retrieve the home page path ( = {@value} )
     */
     String PREFERENCE_HOME_PAGE_PATH      = "home_page_path";
    /**
     * Preference tag to select the font ( = {@value} )
     */
     String PREFERENCE_SELECT_FONT         = "select_font";
    /**
     * Preference tag to the text font ( = {@value} )
     */
     String PREFERENCE_FONT                = "text_font";

    /**
     * minimum text size
     */
     int TEXT_SIZE_MIN = 9;
    /**
     * maximum text size
     */
     int TEXT_SIZE_MAX = 40;

    /**
     * End of PATTERN_LINE setting for Linux files ( = {@value} )
     */
     int EOL_LINUX   = 0;
    /**
     * End of PATTERN_LINE setting for Windows files ( = {@value} )
     */
     int EOL_WINDOWS = 1;
    /**
     * End of PATTERN_LINE setting for Mac files ( = {@value} )
     */
     int EOL_MAC     = 2;

    /**
     * Encoding : 7 bit ASCII ( = {@value} )
     */
     String ENC_ASCII = "US-ASCII";
    /**
     * Encoding : ISO Latin Alphabet, aka ISO-LATIN-1 ( = {@value} )
     */
     String ENC_LATIN = "ISO-8859-1";
    /**
     * Encoding : 8 bit UCS Transformation Format ( = {@value} )
     */
     String ENC_UTF8  = "UTF-8";

    /**
     * color theme default : black on white ( = {@value} )
     */
     int COLOR_CLASSIC  = 0;
    /**
     * color theme negative : white on black ( = {@value} )
     */
     int COLOR_NEGATIVE = 1;
    /**
     * color theme matrix : green on dark green ( = {@value} )
     */
     int COLOR_MATRIX   = 2;
    /**
     * color theme sky : darkblue on skyblue ( = {@value} )
     */
     int COLOR_SKY      = 3;
    /**
     * color theme dracula : red on black ( = {@value} )
     */
     int COLOR_DRACULA  = 4;

    /**
     * Request code for Save As Activity
     */
     int REQUEST_SAVE_AS   = 107;
    /**
     * Request code for Open Activity
     */
     int REQUEST_OPEN      = 108;
    /**
     * Request code for Home Page Activity
     */
     int REQUEST_HOME_PAGE = 109;
    /**
     * Request code for Font Activity
     */
     int REQUEST_FONT      = 110;

     int REQUEST_RECENT = 111;

    /**
     * extra when browsing for file
     */
     String EXTRA_REQUEST_CODE    = "request_code";
    /**
     * Extra to force read only from widget
     */
     String EXTRA_FORCE_READ_ONLY = "force_read_only";

    /**
     * an error result
     */
     int RESULT_ERROR = 1;

    /**
     * Editor(TedFragment.java)
     */
    int REQUEST_FILE    = 112;
    int REQUEST_PROJECT = 113;
}
