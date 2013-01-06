package com.orco.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GlobenInfo extends Activity {
    private WebView browser;

    /** Called when the activity is first created. */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        browser=(WebView)findViewById(R.id.webkit);
        browser.getSettings().setJavaScriptEnabled(true);

        /* WebViewClient must be set BEFORE calling loadUrl! */  
        browser.setWebViewClient(new WebViewClient() {  
            @Override  
            public void onPageFinished(WebView view, String url)  
            {  
                try
                {     
                    browser.loadUrl("javascript:(function() {document.getElementById('usertype').value = '2'; })()");
                    browser.loadUrl("javascript:(function() {document.getElementsByName('ssusername')[0].value = 'emrik.olsson'; })()");
                    browser.loadUrl("javascript:(function() {document.getElementsByName('sspassword')[0].value = 'kfth4yjis6'; })()");
                    browser.loadUrl("javascript:(function() {document.forms['userForm'].submit(); })()");
                }
                catch(Exception ex)
                {
                    Log.e("Error : " , "Error in onPageFinished() " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });  
        
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
    public void onResume() {
      super.onResume();
    }
    
    @Override
    public void onPause() {
      super.onPause();
    }
    
}
