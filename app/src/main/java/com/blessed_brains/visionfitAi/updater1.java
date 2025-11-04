package com.blessed_brains.visionfitAi;

import static com.blessed_brains.visionfitAi.webpagedownloader.staticurl;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class updater1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updater);

    }
    public void updatebtnclicked(View view){
         openUrlInBrowser(staticurl);
        //openWebPage(staticurl);

    }


    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        // Try to set Chrome as target browser
        //intent.setPackage("com.android.chrome");
        // startActivity(intent);
        // Make sure there's an app that can handle this intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No Browser Found !", Toast.LENGTH_SHORT).show();
            // Handle error - no browser found
            // You could show a Toast or log an error
        }
    }
    public void updatecanceled(View v) {
        finish();
    }


    public void openUrlInBrowser(String url) {
        if (url == null || url.trim().isEmpty()) return;

        // ensure scheme exists
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        // optionally show chooser:
        Intent chooser = Intent.createChooser(intent, "Open with");
        try {
            startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            // no browser installed — handle gracefully
            Toast.makeText(this, "No app found to open link", Toast.LENGTH_SHORT).show();
        }
    }





}


