package com.example.maskrcnn;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class AsyncTaskHogSvm extends AsyncTask<Uri, Void, String>{
    Activity contextParent;
    ImageView imageView;
    Spinner sp;
    Integer upsample;
    Dialog dialog;
    TextView processTxt;

    public AsyncTaskHogSvm(Activity contextParent, Integer upsamle) {
        this.contextParent = contextParent;
        this.upsample = upsamle;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sp = contextParent.findViewById(R.id.spinnerModel);
        imageView = contextParent.findViewById(R.id.imageView);
        dialog = new Dialog(contextParent, R.style.MyAlertDialogTheme);
        dialog.setContentView(R.layout.pd_custom);
        processTxt = dialog.findViewById(R.id.loadingTxtView);
        processTxt.setText("Processing with " + sp.getSelectedItem());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected String doInBackground(Uri... imageUri) {
        String imgResultUri = hogSvmFunc(imageUri[0]);
        return imgResultUri;
    }

    private String hogSvmFunc(Uri imageUri) {
        Python python = Python.getInstance();
        PyObject pyFile = python.getModule("hog_face_detector_android");
        File file = new File(getRealPathFromURI(imageUri));
        String imageResult = pyFile.callAttr("execute_model", file.toString(), upsample).toString();
        return imageResult;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = contextParent.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String imageResult) {
        super.onPostExecute(imageResult);
        if (imageResult != null) {
            dialog.hide();
            sendMessage(imageResult);
        }else {
            dialog.show();
        }

    }

    public void sendMessage(String uri) {
        Intent intent = new Intent(contextParent, ResultActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        contextParent.startActivity(Intent.createChooser(intent, null));
    }

}
