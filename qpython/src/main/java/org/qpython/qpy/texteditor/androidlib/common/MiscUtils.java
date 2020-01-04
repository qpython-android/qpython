package org.qpython.qpy.texteditor.androidlib.common;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.qpython.qpy.R;
import org.qpython.qpy.texteditor.widget.crouton.Crouton;
import org.qpython.qpy.texteditor.widget.crouton.Style;

//import static com.thefinestartist.utils.content.ContextUtil.getPackageManager;
//import static com.thefinestartist.utils.content.ContextUtil.startActivity;

/**
 *
 */
public class MiscUtils {

    /**
     * Start an email composer to send an email
     *
     * @param ctx    the current context
     * @param object the title of the mail to compose
     */
    public static void sendEmail(Context ctx, CharSequence object) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("text/plain");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ctx.getResources()
                .getString(R.string.ui_mail)});
        email.putExtra(Intent.EXTRA_SUBJECT, object);
        ctx.startActivity(Intent.createChooser(email,
                ctx.getString(R.string.ui_choose_mail)));
    }

    public static void sendEMailTo(Context ctx,String toAdd, String defaultText) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
//        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, toAdd);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, defaultText);
        if (emailIntent.resolveActivity(ctx.getPackageManager()) != null) {
            ctx.startActivity(emailIntent);
        }
    }

    /**
     * Open the market on my apps
     *
     * @param activity the calling activity
     */
    public static void openMarket(Activity activity) {
        String url;
        Intent market = new Intent(Intent.ACTION_VIEW);
        // market.setData(Uri.parse("market://search?q=pub:Xavier Gouchet"));
        url = activity.getResources().getString(R.string.ui_market_url);
        market.setData(Uri.parse(url));
        try {
            activity.startActivity(market);
        } catch (ActivityNotFoundException e) {
            Crouton.showText(activity, R.string.toast_no_market, Style.ALERT);
        }
    }

    /**
     * Open the market on this app
     *
     * @param activity   the calling activity
     * @param appPackage the application package name
     */
    public static void openMarketApp(Activity activity, CharSequence appPackage) {
        String url;
        Intent market = new Intent(Intent.ACTION_VIEW);
        url = activity.getResources().getString(R.string.ui_market_app_url,
                appPackage);
        market.setData(Uri.parse(url));
        try {
            activity.startActivity(market);
        } catch (ActivityNotFoundException e) {
            Crouton.showText(activity, R.string.toast_no_market, Style.ALERT);
        }
    }

    public static void share(Context context,String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }
}
