package org.qpython.qpy.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hmei
 * 2018/4/19.
 */

public class CodePattern {
    public static final int COLOR_ERROR   = 0x80ff0000;
    public static final int COLOR_NUMBER  = 0xff7ba212;
    public static final int COLOR_KEYWORD = 0xff7ba212;
    public static final int COLOR_BUILTIN = 0xffd79e39;
    public static final int COLOR_COMMENT = 0xff808080;
    public static final int COLOR_QUOTE   = 0xff399ed7;
    
    public static final Pattern PATTERN_LINE       = Pattern.compile(
            ".*\\n");
    public static final Pattern PATTERN_NUMBER     = Pattern.compile(
            "\\b(\\d*[.]?\\d+)\\b");
    public static final Pattern PATTERN_PY_KEYWORD   = Pattern.compile(
            "\\b(break|continue|del|" +
                    "except|exec|finally|" +
                    "pass|print|raise|" +
                    "return|try|with|" +
                    "global|assert|" +
                    "lambda|yield|" +
                    "def|class|self|" +
                    "for|while|" +
                    "if|elif|else|" +
                    "and|in|is|not|or|" +
                    "import|from|as)\\b");
    public static final Pattern PATTERN_LUA_KEYWORD  = Pattern.compile(
            "\\b(and|break|do|" +
                    "else|elseif|end|" +
                    "for|function|" +
                    "goto|if|in|" +
                    "local|" +
                    "not|or|" +
                    "repeat|return|then|" +
                    "until|" +
                    "while)\\b");
    public static final Pattern PATTERN_LUA_BUILD_IN = Pattern.compile(
            "\\b(print|false|true|nil)\\b");
    public static final Pattern PATTERN_PY_BUILD_IN = Pattern.compile(
            "\\b(True|False|bool|enumerate|set|frozenset|help|" +
                    "reversed|sorted|sum|" +
                    "Ellipsis|None|NotImplemented|__import__|abs|" +
                    "apply|buffer|callable|chr|classmethod|cmp|" +
                    "coerce|compile|complex|delattr|dict|dir|divmod|" +
                    "eval|execfile|file|filter|float|getattr|globals|" +
                    "hasattr|hash|hex|id|input|int|intern|isinstance|" +
                    "issubclass|iter|len|list|locals|long|map|max|" +
                    "min|object|oct|open|ord|pow|property|range|" +
                    "raw_input|reduce|reload|repr|round|setattr|" +
                    "slice|staticmethod|str|super|tuple|type|unichr|" +
                    "unicode|vars|xrange|zip|" +
                    "ArithmeticError|AssertionError|AttributeError|" +
                    "DeprecationWarning|EOFError|EnvironmentError|" +
                    "Exception|FloatingPointError|IOError|" +
                    "ImportError|IndentationError|IndexError|" +
                    "KeyError|KeyboardInterrupt|LookupError|" +
                    "MemoryError|NameError|NotImplementedError|" +
                    "OSError|OverflowError|OverflowWarning|" +
                    "ReferenceError|RuntimeError|RuntimeWarning|" +
                    "StandardError|StopIteration|SyntaxError|" +
                    "SyntaxWarning|SystemError|SystemExit|TabError|" +
                    "TypeError|UnboundLocalError|UnicodeError|" +
                    "UnicodeEncodeError|UnicodeDecodeError|" +
                    "UnicodeTranslateError|" +
                    "UserWarning|ValueError|Warning|WindowsError|" +
                    "ZeroDivisionError)\\b");
    public static final Pattern PATTERN_PY_COMMENT  = Pattern.compile(
            "/\\*(?:.|[\\n\\r])*?\\*/|" +
                    "#.*\n|" +
                    "\"\"\"(?:.|[\\n\\r])*?\"\"\"|" +
                    "\'\'\'(?:.|[\\n\\r])*?\'\'\'");

    public static SpannableString formatPyCode(String code) {
        SpannableString sStr = new SpannableString(code);
        for (Matcher m = PATTERN_PY_KEYWORD.matcher(sStr); m.find(); ) {
            sStr.setSpan(
                    new ForegroundColorSpan(COLOR_KEYWORD),
                    m.start(),
                    m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (Matcher m = PATTERN_PY_BUILD_IN.matcher(sStr); m.find(); ) {
            sStr.setSpan(
                    new ForegroundColorSpan(COLOR_BUILTIN),
                    m.start(),
                    m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (Matcher m = PATTERN_PY_COMMENT.matcher(sStr); m.find(); ) {
            sStr.setSpan(
                    new ForegroundColorSpan(COLOR_COMMENT),
                    m.start(),
                    m.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sStr;
    }
}
