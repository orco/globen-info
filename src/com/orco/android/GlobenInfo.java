package com.orco.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

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
        try
        {     
            browser.loadUrl("file:///android_asset/globen.html");
//            browser.loadUrl("https://sms11.schoolsoft.se/globen/jsp/Login.jsp");  
//            StringBuilder buf=new StringBuilder("javascript:setUserName('muppen')");
//            browser.loadUrl(buf.toString());
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
//      myLocationManager.requestLocationUpdates(PROVIDER, 0,
//                                                0,
//                                                onLocation);
    }
    
    @Override
    public void onPause() {
      super.onPause();
//      myLocationManager.removeUpdates(onLocation);
    }
    
}
