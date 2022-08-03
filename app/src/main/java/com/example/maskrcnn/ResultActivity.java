package com.example.maskrcnn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class ResultActivity extends AppCompatActivity {

    ImageView imageViewResult;
    TextView txtViewResult;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);



        imageViewResult = findViewById(R.id.imageViewResult);
        txtViewResult = findViewById(R.id.textViewResult);

        Intent intent = getIntent();

        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            txtViewResult.setText(imageUri.toString());
            imageViewResult.setImageURI(imageUri);
        }


    }
}
