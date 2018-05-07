package com.indomaret.klikindomaret;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class JSInterface {
    Context mContext;

    JSInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void showToast(String toast){
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void updateTotalItemCart(String total){
        MainActivity activity = (MainActivity) mContext;
        activity.updateCartTotal(Integer.parseInt(total));
    }

    @JavascriptInterface
    public void openAndroidDialog(){
        AlertDialog.Builder myDialog
                = new AlertDialog.Builder(mContext);
        myDialog.setTitle("DANGER!");
        myDialog.setMessage("You can do what you want!");
        myDialog.setPositiveButton("ON", null);
        myDialog.show();
    }

    @JavascriptInterface
    public void clearCookies() {
        final MainActivity activity = (MainActivity) mContext;
        final WebView web = activity.mainWebview;

        web.post(new Runnable() {
            @Override
            public void run() {
                web.clearCache(true);
            }
        });

        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getResources().getString(R.string.pref_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();

        Log.d("Logout", "Load Side Menu");
        activity.loadSideMenu();
    }

    @JavascriptInterface
    public void pushCustomerDevice() {
        MainActivity activity = (MainActivity) mContext;
//        activity.sendDeviceID();
    }

    @JavascriptInterface
    public void showPreloader() {
        MainActivity activity = (MainActivity) mContext;
//        activity.showPreloaderAnimation();
    }

    @JavascriptInterface
    public void sendEmail(String subject,String message) {
        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, message);

        try {
            mContext.startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

//    @JavascriptInterface
//    public void getFirstName(String name){
//        MainActivity activity = (MainActivity) mContext;
//        activity.updateName(name);
//    }
}