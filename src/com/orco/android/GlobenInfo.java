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
import android.widget.Toast;

public class GlobenInfo extends Activity {
    private WebView browser;
    private String username;
    private String password;
    private Boolean automaticallyLogin;
    private final String SchoolSoftUrl = "https://sms11.schoolsoft.se/globen/jsp/pda/Login.jsp";
    private Integer loginAttemptsLeft = 1;
    private String lastUrl = "";
    private Boolean tryingToLogin = false;

    /** Called when the activity is first created. */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        browser=(WebView)findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        loadPrefs();
        injectJavaScript();
        loadSchoolsoft();
    }


    private void injectJavaScript() {
        /* WebViewClient must be set BEFORE calling loadUrl! */  
        browser.setWebViewClient(new WebViewClient() {  
            @Override  
            public void onPageFinished(WebView view, String url) {
                final Boolean onLoginPage   = url.equals(SchoolSoftUrl);
                final Boolean lastWasLogin  = lastUrl.equals(SchoolSoftUrl);
                final Boolean loginSucceded = !onLoginPage && lastWasLogin;
                lastUrl = url;


                /* If we are on another page and not just logged in, just do nothing*/
                if (!onLoginPage && !lastWasLogin) {
                    return;
                }

                if (loginSucceded) {
                    Toast.makeText(getApplicationContext(), "Inloggad!",
                            Toast.LENGTH_SHORT).show();
                    loginAttemptsLeft = 1;
                    return;
                }

                if (onLoginPage) {
                    if (tryingToLogin) {
                        Toast.makeText(
                                getApplicationContext(),
                                "Misslyckades logga in, kontrollera användarnamn och lösenord!",
                                Toast.LENGTH_LONG).show();
                        tryingToLogin = false;
                        startPrefActivity();
                    } else {
                        try {
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
                            if (automaticallyLogin && (loginAttemptsLeft > 0)) {
                                tryingToLogin = true;
                                loginAttemptsLeft--;
                                browser.loadUrl(jsUrlStart
                                        + "forms['userForm'].submit()"
                                        + jsUrlEnd);
                            }
                        } catch (Exception ex) {
                            Log.e("Error : ",
                                    "Error in onPageFinished() "
                                            + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });  

    }

    private void loadSchoolsoft() {
        try
        {     
            browser.loadUrl(SchoolSoftUrl);
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
        loadPrefs();
        injectJavaScript();
        loadSchoolsoft();
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }

    private boolean validConfig(String cfg) {
        return !(cfg.equals("UNKNOWN") || cfg.equals(""));
    }

    private void loadPrefs() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        String userPref = sharedPrefs.getString("username", "UNKNOWN");
        String passPref = sharedPrefs.getString("password", "UNKNOWN");
        if (!(validConfig(userPref) && validConfig(passPref))) {
            startPrefActivity();
            return;
        }
        automaticallyLogin = sharedPrefs.getBoolean("autoLogin", false);
        if (!userPref.equals(username) || !passPref.equals(password)) {
            if (automaticallyLogin) {
                loginAttemptsLeft = 1;
            }
        }
        username = userPref;
        password = passPref;
    }
}
