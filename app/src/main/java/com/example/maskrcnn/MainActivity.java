package com.example.maskrcnn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    public static final String EXTRA_MESSAGE = "com.example.maskrcnn.MESSAGE";
    Button choseImgBtn;
    Button processBtn;
    ImageView imageView;
    TextView txtView;
    Spinner spinnerModel;
    ArrayList<Uri> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        choseImgBtn = findViewById(R.id.button_chose_image);
        imageView = findViewById(R.id.imageView);
        processBtn = findViewById(R.id.processBtn);
        txtView = findViewById(R.id.textView);
        spinnerModel = findViewById(R.id.spinnerModel);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.models_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModel.setAdapter(adapter);

        //Set button listener
//        choseImgBtn.setOnClickListener( view -> {
//            String [] strings = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
//            if(EasyPermissions.hasPermissions(this, strings)){
//                imagePicker();
//            }else {
//                EasyPermissions.requestPermissions(
//                        this,
//                        "Cần cung cấp quyền truy cập và thư viện cho ứng dụng!",
//                        100,
//                        strings
//                );
//            }
//        });

        imageView.setOnClickListener( view -> {
            String [] strings = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
            if(EasyPermissions.hasPermissions(this, strings)){
                imagePicker();
            }else {
                EasyPermissions.requestPermissions(
                        this,
                        "Cần cung cấp quyền truy cập và thư viện cho ứng dụng!",
                        100,
                        strings
                );
            }
        });

        processBtn.setOnClickListener( view -> {
//            Integer text = spinnerModel.getSelectedItemPosition();
//            txtView.setText(text.toString());
            sendMessage();
        });
    }

    public void sendMessage() {
        Intent intent = new Intent(this, ResultActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, arrayList.get(0));
        intent.setType("image/jpeg");
        startActivity(Intent.createChooser(intent, null));
    }


    private String getPythonFunc() {
        Python python = Python.getInstance();
        PyObject pyFile = python.getModule("test");
        return pyFile.callAttr("hellopy", "DCM").toString();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data != null){
            if(requestCode == FilePickerConst.REQUEST_CODE_PHOTO){
                arrayList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                imageView.setImageURI(arrayList.get(0));
            }
        }
    }

    private void imagePicker() {
        FilePickerBuilder.getInstance()
                .setActivityTitle("Chọn ảnh")
                .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
                .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
                .setMaxCount(1)
                .setSelectedFiles(arrayList)
                .setActivityTheme(R.style.CustomTheme)
                .pickPhoto(this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(requestCode == 100 && perms.size() == 2){
            imagePicker();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)){
            new AppSettingsDialog.Builder(this).build().show();
        }else{
            Toast.makeText(getApplicationContext(), "Permission Denied",Toast.LENGTH_SHORT).show();
        }
    }
}