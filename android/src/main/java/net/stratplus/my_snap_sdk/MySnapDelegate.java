package net.stratplus.my_snap_sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.Base64;


import com.miteksystems.misnap.misnapworkflow.params.WorkflowApi;
import com.miteksystems.misnap.misnapworkflow_UX2.MiSnapWorkflowActivity_UX2;
import com.miteksystems.misnap.params.CameraApi;
import com.miteksystems.misnap.params.MiSnapApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

public class MySnapDelegate implements PluginRegistry.ActivityResultListener{
    private final Activity activity;
    private Context context;
    private MethodChannel.Result pendingResult;
    private Bitmap imageBitmap;



    public MySnapDelegate(final Activity activity, Context context){
        Log.d("Delegate", "Constructor delegate");
        this.activity = activity;
        this.context = context;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        byte[] rawImage = data.getByteArrayExtra(MiSnapApi.RESULT_PICTURE_DATA);
        if (rawImage == null || rawImage.length == 0) {
            Log.d("ActivityResult", "NO RAW DATA");
            return false;
        }


        //pendingResult.success("Android READY MDFCK!");
        //result.success("Android " + android.os.Build.VERSION.RELEASE);

        //if (MiSnapApi.RESULT_PICTURE_CODE == requestCode) {
        String b64Image = encodeToBase64(rawImage);

        String mibi = data.getStringExtra(MiSnapApi.RESULT_MIBI_DATA);

        Log.d("MIBI:", mibi);
        //Log.d("IMAGE:", b64Image);
        //}

        imageBitmap = formBitmapImage(rawImage);
        byte[] B64 = bitmapToPngB64(imageBitmap);
        Log.d("B64Len", B64.length+"");

        //Log.d("IMAGE_PNG:", B64);

        pendingResult.success(B64);


        return false;
    }



    public static String encodeToBase64(byte[] jpegData)
    {

        String imageEncoded = Base64.encodeToString(jpegData, Base64.DEFAULT);
        return imageEncoded;
    }


    private boolean setPendingResult(MethodChannel.Result result){
        pendingResult = result;
        return true;
    }

    private Bitmap formBitmapImage(byte[] byteImage) {
        System.gc();
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap sourceBmp = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length, options);
        return sourceBmp;
    }

    public static byte[] bitmapToPngB64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        //String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        //return imageEncoded;

        return b;
    }




    public void startMiSnapWorkflow(String docType, MethodChannel.Result result) {
        try {
            JSONObject misnapParams = new JSONObject();
            misnapParams.put(MiSnapApi.MiSnapDocumentType, docType);
            misnapParams.put(WorkflowApi.MiSnapTrackGlare, "1");
            misnapParams.put(CameraApi.MiSnapFocusMode, CameraApi.PARAMETER_FOCUS_MODE_HYBRID);
            Intent intentMiSnap = new Intent(context, MiSnapWorkflowActivity_UX2.class);
            intentMiSnap.putExtra(MiSnapApi.JOB_SETTINGS, misnapParams.toString());
            System.out.println("Running MiSnap...");
            setPendingResult(result);
            this.activity.startActivityForResult(intentMiSnap, MiSnapApi.RESULT_PICTURE_CODE);

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
}
