package com.example.maskrcnn;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    public static final String EXTRA_MESSAGE = "com.example.maskrcnn.MESSAGE";
    Button choseImgBtn;
    Button processBtn;
    ImageView imageView;
    TextView txtView;
    Spinner spinnerModel;
    Dialog dialog;
    Uri uri;
    AsyncTaskHogSvm myAsyncTask;
    ArrayList<Uri> arrayList = new ArrayList<>();
    ImageView textViewRs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new Dialog(MainActivity.this, R.style.MyAlertDialogTheme);
        dialog.setContentView(R.layout.pd_custom);

//        textViewRs = findViewById(R.id.textViewRS);
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
        choseImgBtn.setOnClickListener(view ->{
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
            Integer indexSpinner = spinnerModel.getSelectedItemPosition();
            if(uri == null){
                Toast.makeText(this, "Bạn chưa chọn ảnh!", Toast.LENGTH_SHORT).show();
            }else {
                if(indexSpinner == 0){
                    myAsyncTask = new AsyncTaskHogSvm(MainActivity.this, 2);
                    myAsyncTask.execute(uri);
                }else if (indexSpinner == 1){
                    myAsyncTask = new AsyncTaskHogSvm(MainActivity.this, 3);
                    myAsyncTask.execute(uri);
                }else {
                    myAsyncTask = new AsyncTaskHogSvm(MainActivity.this, 0);
                    myAsyncTask.execute(uri);
                }
            }
        });
    }

    ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                if (result.getResultCode() == RESULT_OK) {
                    uri = result.getData().getData();
                    imageView.setImageURI(uri);
                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(getApplicationContext(), "Không có ảnh nào được chọn!", Toast.LENGTH_SHORT).show();
                }
            });

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("resultCode", String.valueOf(resultCode));
////        if(requestCode == 1080523822 && resultCode == Activity.RESULT_OK){
////            uri = data.getData();
////            imageView.setImageURI(uri);
////        }else {
////            Toast.makeText(getApplicationContext(), "No Image selected!", Toast.LENGTH_SHORT).show();
////        }
//    }

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
                uri = arrayList.get(0);
                imageView.setImageURI(uri);
            }
        }
    }

    private void imagePicker2() {
        ImagePicker.Companion.with(MainActivity.this)
                .maxResultSize(512,512,true)
                .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                .createIntentFromDialog((Function1)(new Function1(){
                    public Object invoke(Object var1) {
                        this.invoke((Intent) var1);
                        return Unit.INSTANCE;
                    }

                    public final void invoke(@NotNull Intent it) {
                        Intrinsics.checkNotNullParameter(it, "it");
                        launcher.launch(it);
                    }
                }));
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