package com.orco.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class GlobenInfo extends Activity {
    private WebView browser;
    private String username;
    private String password;
    private Boolean automaticallyLogin = false;
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
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        browser=(WebView)findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
        if (loadPrefs()) {
            injectJavaScript();
            loadSchoolsoft(SchoolSoftUrl);
            browser.setDownloadListener(new DownloadListener()
            {
                public void onDownloadStart(String url,      String userAgent, String contentDisposition,
                                            String mimeType, long   contentLength)
                {
                    Toast.makeText(getApplicationContext(), "Laddar ner: " + url,
                            Toast.LENGTH_SHORT).show();

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                    CookieManager mgr = CookieManager.getInstance();
                    Log.i( "URL", url );
                    Log.i("Cookie",mgr.getCookie("sms11.schoolsoft.se"));
                    String cookie_string = mgr.getCookie(SchoolSoftUrl);
                    String sessionid = "";
                    for(String cookie: cookie_string.split(";")) {
                        if(cookie.split("=")[0].equals(" JSESSIONID")) {
                            sessionid = cookie.split("=")[1];
                        }
                    }
                    String title = "";
                    for(String s: contentDisposition.split(";")) {
                        if(s.split("=")[0].equals(" filename")) {
                            title = s.split("=")[1];
                        }
                    }

                    request.setDescription(title.replaceAll("\\s", "").replaceAll("[^\\x00-\\x7F]", ""));
                    request.setMimeType(mimeType);
                    request.setTitle("Nerladdning");
                    request.addRequestHeader("Cookie", "JSESSIONID=" + sessionid);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    manager.enqueue(request);
                }
            });
        }
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
                    tryingToLogin = false;
                    return;
                }

                if (onLoginPage) {
                    if (tryingToLogin) {
                        Toast.makeText(
                                getApplicationContext(),
                                "Misslyckades logga in med "
                                        + username
                                        + " "
                                        + password
                                        + ", kontrollera anv�ndarnamn och l�senord!",
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
                                Log.i("subMitting attempts left",
                                        loginAttemptsLeft.toString());
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

    private void loadSchoolsoft(String url) {
        try
        {     
            Log.i("Loading ", url);
            browser.loadUrl(url);
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
        startActivityForResult(intent, 42);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42) {
            if (loadPrefs()) {
                injectJavaScript();
                loadSchoolsoft(SchoolSoftUrl);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }

    private boolean validConfig(String cfg) {
        return !(cfg.equals("UNKNOWN") || cfg.equals(""));
    }

    private Boolean loadPrefs() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        String userPref = sharedPrefs.getString("username", "UNKNOWN");
        String passPref = sharedPrefs.getString("password", "UNKNOWN");
        automaticallyLogin = sharedPrefs.getBoolean("autoLogin", false);
        if (!(validConfig(userPref) && validConfig(passPref))) {
            Log.e("Invalid username or password: ", userPref + " " + passPref);
            startPrefActivity();
            return false;
        }
        if (!userPref.equals(username) || !passPref.equals(password)) {
            if (automaticallyLogin) {
                loginAttemptsLeft = 1;
            }
        }
        username = userPref;
        password = passPref;
        return true;
    }
}
