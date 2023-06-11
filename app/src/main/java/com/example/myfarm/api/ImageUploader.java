package com.example.myfarm.api;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUploader extends AsyncTask<Bitmap, Void, String> {
    private static final String API_ENDPOINT = "http://localhost:5000/predict";
    private ImageUploadListener uploadListener;
    private final static String TAG="ImageUploader";
    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        Bitmap bitmap = bitmaps[0];
        try {
            URL url = new URL(API_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            // Convert Bitmap to byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            // Set request headers
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("Content-Length", String.valueOf(imageBytes.length));
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(imageBytes);
            outputStream.flush();
            outputStream.close();
            // Get response from the API
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream responseStream = connection.getInputStream();
                // Process the response from the API if needed
                responseStream.close();
            } else {
                InputStream errorStream = connection.getErrorStream();
                // Handle the error response if needed
                Log.e(TAG,errorStream.toString());
                errorStream.close();
            }

            connection.disconnect();
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e(TAG,e.toString());
            return null;
        }

        return "Image uploaded successfully";
    }

    @Override
    protected void onPostExecute(String result) {
        // Handle the result or display any error messages
        if (uploadListener != null) {
            if (result != null) {
                Log.d(TAG, result);
                uploadListener.onImageUploadResult(result);
            } else {
                Log.e("ImageUploader", "Failed to upload image");
                uploadListener.onImageUploadResult("Failed to upload image");
            }

        }

    }


}
