package com.blessed_brains.visionfitAi;

import static com.blessed_brains.visionfitAi.MainActivity.Appversion;
import static com.blessed_brains.visionfitAi.MainActivity.mainActivity1;

import android.content.Intent;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class webpagedownloader {
    String text = "";
    static String staticurl = null;
    Boolean isupdateuvailable = false;
    String downloadlink = null;
    String version = null;
    public void Isupdateavailable() {
        new Thread() {
            @Override
            public void run() {

               // Log.d("gouravhttp", "version" );
                try {
                    // Connect to the website
                    Document doc = Jsoup.connect("https://cdn.jsdelivr.net/gh/impossible589/visionfitai@main/update.html").get();

                    // Extract only text (no HTML tags)
                    text = doc.text();

                    // Print plain text content

                } catch (Exception e) {

                    text = "Network_Error";
                    Log.d("gouravhttp", "Network_Error"+ e);
                }
                if (text.contains("xy345")) {
                    isupdateuvailable = true;
                    String[] url = text.split("xy345");
                    String[] url2 = url[1].split("version:");
                    downloadlink = url2[0];
                    staticurl = downloadlink;
                    Log.d("gouravhttp", "downloadlink" + downloadlink);
                    Log.d("gouravhttp", "version" + url2[1]);

                    version = url2[1];
                    if(Appversion < Integer.parseInt(version)){


                    mainActivity1.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity1.startActivity(new Intent(mainActivity1.getApplicationContext(), updater1.class));
                           //mainActivity1.setContentView(R.layout.updater);


                        }
                    });}
                    text =  url[1];
                } else {
                    text = "No";
                    Log.d("gouravhttp", "No update available");
                }

            }
        }.start();



    }
}
