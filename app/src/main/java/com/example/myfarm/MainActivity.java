package com.example.myfarm;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myfarm.api.ImageUploadListener;
import com.example.myfarm.api.ImageUploader;
import com.example.myfarm.api.ImageUploadListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int REQUEST_CODE_SELECT_IMAGE = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_INTERNET = 2;
    private static final String TAG="MainActivity";
    private Button cameraButton;
    private Button leafDiseaseButton;
    private Button pestDiseaseButton;
    private ImageView imageView;
    private OkHttpClient client;
    private static final String END_API="http://192.168.1.51:5000/test";
    private static final String END_POST_API="http://192.168.1.51:5000/predict";
    private Bitmap mBitmap;
    private TextView mPercent,mDisease;
    private Button mGallaryBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET);

        cameraButton = findViewById(R.id.camera_button);
        leafDiseaseButton = findViewById(R.id.leaf_disease);
        leafDiseaseButton.setVisibility(View.GONE);
        leafDiseaseButton.setOnClickListener(this);


        pestDiseaseButton = findViewById(R.id.pest_disease);
        pestDiseaseButton.setVisibility(View.GONE);
        pestDiseaseButton.setOnClickListener(this);

        mGallaryBtn=findViewById(R.id.chose_galary);
        mGallaryBtn.setOnClickListener(this);



        imageView = findViewById(R.id.image_view);

        mPercent=findViewById(R.id.percent);
        mDisease=findViewById(R.id.name_disease);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    launchCamera();
                }
            }
        });
    }
    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            mBitmap=imageBitmap;
            return;
        }
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                    pestDiseaseButton.setVisibility(View.VISIBLE);
                    leafDiseaseButton.setVisibility(View.VISIBLE);
                    mBitmap=bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                }
            }
        }

        // TO DO : add handle exception here
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_INTERNET) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Access to Internet Permission Granted. Thanks.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Access to Internet Permission Denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void sendImage(Bitmap bitmap,String type) {
        // Create OkHttp client
      //  OkHttpClient client = new OkHttpClient();
        Log.i(TAG,type);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                .build();

        // Convert Bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        // Create request body
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "image.jpg",
                        RequestBody.create(MediaType.parse("image/jpeg"), imageData))
                .addFormDataPart("type",type)
                .build();
        // Create request
        Request request = new Request.Builder()
                .url(END_POST_API)
                .post(requestBody)
                .build();
        // Execute the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,e.getMessage());
                // Handle failure
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle success
                if (response.isSuccessful()) {
                    if (response.body()!=null)
                    {
                        String responseData = response.body().string();
                        Log.i(TAG,responseData);
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            String diseaseName=jsonObject.getString("Disease Name:");
                            double percent=jsonObject.getDouble("Percentage ");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mDisease.setText(diseaseName);
                                    mPercent.setText(Integer.toString((int) percent));

                                }
                            });
                        } catch (JSONException e) {
                            Log.e(TAG,e.getMessage());
                        }
                    }
                    else{
                        Log.i(TAG,"reponse body is null");
                    }

                    // Process the response data
                } else {
                    // Handle unsuccessful response
                    String errorMessage = response.message();
                    Log.i(TAG,errorMessage);
                    // Process the error message
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        int id=view.getId();
        if (R.id.leaf_disease==id){
            // call leaf_diseases
            Log.i(TAG,"leaf disease button was clicked! ");
            sendImage(mBitmap,"leaf_disease");
            return;
        }
        if (R.id.pest_disease==id){

            Log.i(TAG,"pest disease button was clicked! ");
            Toast.makeText(this, "We are comming soon !", Toast.LENGTH_SHORT).show();
            sendImage(mBitmap,"pest_disease");
            return;
        }
        if (R.id.chose_galary==id){

            Log.i(TAG,"Gallary Button was clicked! ");
            selectImageFromGallery();
            Toast.makeText(this, "We are comming soon !", Toast.LENGTH_SHORT).show();
            return;
        }

    }
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

}
