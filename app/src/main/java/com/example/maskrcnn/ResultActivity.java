package com.example.maskrcnn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;


public class ResultActivity extends AppCompatActivity {

    ImageView imageViewResult;
    TextView txtViewResult;
    TextView txtViewBack;
    String numFace = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtViewBack = findViewById(R.id.textViewBack);
        imageViewResult = findViewById(R.id.imageViewResult);
        txtViewResult = findViewById(R.id.textViewResult);

        Intent intent = getIntent();
        String imageUri = intent.getExtras().getString("Intent.EXTRA_STREAM");
        numFace = intent.getExtras().getString("NUMFACE");
        if (imageUri != null) {
            txtViewResult.setText("Số gương mặt: " + numFace);
            File imgFile = new File(imageUri);

            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageViewResult.setImageBitmap(myBitmap);
            }
        }

        txtViewBack.setOnClickListener(view -> {
            this.finish();
        });


    }
}
