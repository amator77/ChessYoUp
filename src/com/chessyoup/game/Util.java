package com.chessyoup.game;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.chessyoup.game.view.ColorTheme;
import com.cyp.chess.model.Piece;
import com.cyp.chess.model.Position;

public final class Util {
    public final static String boldStart;
    public final static String boldStop;

    static {
        // Using bold face causes crashes in android 4.1, see:
        // http://code.google.com/p/android/issues/detail?id=34872
        final int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
        if (sdkVersion >= 16) {
            boldStart = "";
            boldStop = "";
        } else {
            boldStart = "<b>";
            boldStop = "</b>";
        }
    }

    /** Read a text file. Return string array with one string per line. */
    public static String[] readFile(String networkEngineToConfig) throws IOException {
        ArrayList<String> ret = new ArrayList<String>();
        InputStream inStream = new FileInputStream(networkEngineToConfig);
        InputStreamReader inFile = new InputStreamReader(inStream);
        BufferedReader inBuf = new BufferedReader(inFile);
        String line;
        while ((line = inBuf.readLine()) != null)
            ret.add(line);
        inBuf.close();
        return ret.toArray(new String[ret.size()]);
    }

    /** Represent materiel difference as two unicode strings. */
    public final static class MaterialDiff {
        public CharSequence white;
        public CharSequence black;
        MaterialDiff(CharSequence w, CharSequence b) {
            white = w;
            black = b;
        }
    }

    /** Compute material difference for a position. */
    public static MaterialDiff getMaterialDiff(Position pos) {
        StringBuilder whiteString = new StringBuilder();
        StringBuilder blackString = new StringBuilder();
        for (int p = Piece.WPAWN; p >= Piece.WKING; p--) {
            int diff = pos.nPieces(p) - pos.nPieces(Piece.swapColor(p));
            while (diff < 0) {
                whiteString.append(Piece.toUniCode(Piece.swapColor(p)));
                diff++;
            }
            while (diff > 0) {
                blackString.append(Piece.toUniCode(p));
                diff--;
            }
        }
        return new MaterialDiff(whiteString, blackString);
    }

    /** Enable/disable full screen mode for an activity. */
    public static void setFullScreenMode(Activity a, SharedPreferences settings) {
        boolean fullScreenMode = settings.getBoolean("fullScreenMode", false);
        WindowManager.LayoutParams attrs = a.getWindow().getAttributes();
        if (fullScreenMode) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        a.getWindow().setAttributes(attrs);
    }

    /** Change foreground/background color in a view. */
    public static void overrideFonts(final View v) {
        if (v == null)
            return;
        final int bg = ColorTheme.instance().getColor(ColorTheme.GENERAL_BACKGROUND);
        final boolean excludedItems = v instanceof Button ||
                                      v instanceof EditText ||
                                      v instanceof ImageButton ||
                                      "title".equals(v.getTag());
        if (!excludedItems)
            v.setBackgroundColor(bg);
        if (v instanceof ListView)
            ((ListView) v).setCacheColorHint(bg);
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                overrideFonts(child);
            }
        } else if ((v instanceof TextView) && !excludedItems) {
            int fg = ColorTheme.instance().getColor(ColorTheme.FONT_FOREGROUND);
            ((TextView) v).setTextColor(fg);
        }
    }
}
