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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class AsyncTaskHogSvm extends AsyncTask<Uri, Void, String>{
    Activity contextParent;
    ImageView imageView;
    Spinner sp;
    Integer upsample;
    Dialog dialog;
    TextView processTxt;
    TextView textViewRs;
    String resultUri;

    public AsyncTaskHogSvm(Activity contextParent, Integer upsamle) {
        this.contextParent = contextParent;
        this.upsample = upsamle;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sp = contextParent.findViewById(R.id.spinnerModel);
        imageView = contextParent.findViewById(R.id.imageView);
        textViewRs = contextParent.findViewById(R.id.textViewRS);
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
        String numFace = hogSvmFunc(imageUri[0]);
        File file = new File(getRealPathFromURI(imageUri[0]));
        resultUri =  processRsUri(file.toString());
        return numFace;
    }

    private String hogSvmFunc(Uri imageUri) {
        Python python = Python.getInstance();
        File file = new File(getRealPathFromURI(imageUri));
        resultUri =  processRsUri(file.toString());
        String numFace = "";
        if(upsample == 0){
            PyObject pyFile = python.getModule("mtcnn_face_detector_android");
            numFace = pyFile.callAttr("execute_model", file.toString()).toString();
        }else {
            PyObject pyFile = python.getModule("hog_face_detector_android");
            numFace = pyFile.callAttr("execute_model", file.toString(), upsample).toString();
        }
        return numFace;
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
    protected void onPostExecute(String numFace) {
        super.onPostExecute(numFace);
        if (numFace != null) {
            dialog.hide();
            if(Integer.valueOf(numFace) == 0){
                Toast.makeText(contextParent, "Không tìm thấy gương mặt nào!!", Toast.LENGTH_SHORT).show();
            }else if (Integer.valueOf(numFace) > 0) {
//                sendMessage(resultUri,numFace);
                textViewRs.setText("Số gương mặt: "+ numFace);
                File imgFile = new File(resultUri);
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
            } else {

            }
        }else {
            dialog.show();
        }

    }

    public void sendMessage(String uri, String numFace) {
        Intent intent = new Intent(contextParent, ResultActivity.class);
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra("Intent.EXTRA_STREAM", uri);
        intent.putExtra("NUMFACE", numFace);
        contextParent.startActivity(Intent.createChooser(intent, null));
    }

    private String processRsUri(String input) {
        Log.e("INPUT", input);
        String extension = "";
        if (input.contains(".jpg")) {
            extension = ".jpg";
        } else if (input.contains(".jpeg")) {
            extension = ".jpeg";
        } else if (input.contains(".png")) {
            extension = ".png";
        } else if (input.contains(".webp")) {
            extension = ".webp";
        }
        String output = input.replace(extension, "_model_output" + extension);
        Log.e("OUTPUT", output);

        return output;
    }

}
