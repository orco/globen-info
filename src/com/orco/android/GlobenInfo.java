package com.orco.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GlobenInfo extends Activity {
    private WebView browser;
    private String username;
    private String password;

    /** Called when the activity is first created. */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        browser=(WebView)findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        loadPrefs();

        /* WebViewClient must be set BEFORE calling loadUrl! */  
        browser.setWebViewClient(new WebViewClient() {  
            @Override  
            public void onPageFinished(WebView view, String url)  
            {  
                try
                {     
                    String jsUrlStart = "javascript:(function() {document.";
                    String jsUrlEnd = "; })()";
                    browser.loadUrl(jsUrlStart
                            + "getElementById('usertype').value = '2'"
                            + jsUrlEnd);
                    browser.loadUrl(jsUrlStart
                            + "getElementsByName('ssusername')[0].value = '"
                            + username + "'" + jsUrlEnd);
                    browser.loadUrl(jsUrlStart
                            + "getElementsByName('sspassword')[0].value = '"
                            + password + "'" + jsUrlEnd);
                    // browser.loadUrl("javascript:(function() {document.forms['userForm'].submit(); })()");
                }
                catch(Exception ex)
                {
                    Log.e("Error : " , "Error in onPageFinished() " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });  
        loadSchoolsoft();
    }

    private void loadSchoolsoft() {
        try
        {     
            //browser.loadUrl("file:///android_asset/globen.html");
            browser.loadUrl("https://sms11.schoolsoft.se/globen/jsp/pda/Login.jsp");  
        }
        catch(Exception ex)
        {
            Log.e("Error : " , "Error in onCreate() " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
         * Because it's only ONE option in the menu. In order to make it simple,
         * We always start SetPreferenceActivity without checking.
         */
        startPrefActivity();
        return true;
    }

    private void startPrefActivity() {
        Intent intent = new Intent();
        intent.setClass(GlobenInfo.this, SetPreferenceActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onResume() {
      super.onResume();
        loadSchoolsoft();
    }
    
    @Override
    public void onPause() {
      super.onPause();
    }

    private boolean validConfig(String cfg) {
        return (!(cfg.equals("UNKNOWN")) && !(cfg.equals("")));
    }

    private void loadPrefs() {
        SharedPreferences mySharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        username = mySharedPreferences.getString("username", "UNKNOWN");
        password = mySharedPreferences.getString("password", "UNKNOWN");
        // Log.e("username = ", username);
        // Log.e("password = ", password);
        if (!validConfig(username) || !validConfig(password)) {
            startPrefActivity();
        }
    }
}
