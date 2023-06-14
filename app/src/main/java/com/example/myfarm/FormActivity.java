package com.example.myfarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mNitrigenText;
    private EditText mPhosohoText;
    private EditText mPotText;
    private EditText mPHText;
    private EditText mRainText;
    private EditText mTempText;
    private EditText mHumText;
    private static final String END_POST_API="http://192.168.1.51:5000/soule";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        mNitrigenText=findViewById(R.id.Nitrogen);
        mPhosohoText=findViewById(R.id.Phosphorous);
        mPotText=findViewById(R.id.Potassium);
        mPotText=findViewById(R.id.pH_level);
        mPHText=findViewById(R.id.Rainful);
        mTempText=findViewById(R.id.temperature);
        mHumText=findViewById(R.id.humidity);
    }

    @Override
    public void onClick(View view) {
    }

    public static void sendFormData() {
        OkHttpClient client = new OkHttpClient();

        // Build the request body
        RequestBody formBody = new FormBody.Builder()
                .add("param1", "value1")
                .add("param2", "value2")
                .add("param3", "value3")
                .add("param4", "value4")
                .add("param5", "value5")
                .add("param6", "value6")
                .add("param7", "value7")
                .build();

        // Build the request
        Request request = new Request.Builder()
                .url("http://example.com/submit")
                .post(formBody)
                .build();

        // Send the request
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println("Form data sent successfully");
                // Process the response here
            } else {
                System.out.println("Form data sending failed");
                // Handle unsuccessful response
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle network or IO error
        }
    }

}