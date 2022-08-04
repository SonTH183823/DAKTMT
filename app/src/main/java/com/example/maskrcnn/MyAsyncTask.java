package com.example.maskrcnn;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.File;

public class MyAsyncTask extends AsyncTask<Uri, Void, String> {
    Activity contextParent;
    ImageView imageView;
    Dialog dialog;

    public MyAsyncTask(Activity contextParent) {
        this.contextParent = contextParent;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        imageView = contextParent.findViewById(R.id.imageView);
        dialog = new Dialog(contextParent, R.style.MyAlertDialogTheme);
        dialog.setContentView(R.layout.pd_custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
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
        String imageResult = pyFile.callAttr("execute_model", file.toString(), 2).toString();
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
        Log.e("RESULT", imageResult);
        if (imageResult != null) {
            dialog.hide();
            File imgFile = new File(imageResult);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }
        }else {
            dialog.show();
        }

    }
}
