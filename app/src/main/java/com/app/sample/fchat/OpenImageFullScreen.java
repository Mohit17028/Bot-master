package com.app.sample.fchat;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class OpenImageFullScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image_full_screen);

        ImageView fullPhoto_iv = (ImageView) findViewById(R.id.fullPhoto_iv);
        Intent callingActivityIntent = getIntent();
        if (callingActivityIntent != null){
            Uri imageUri = callingActivityIntent.getData();
            if(imageUri != null && fullPhoto_iv != null){
                fullPhoto_iv.setImageURI(imageUri);
            }
        }
    }
}
