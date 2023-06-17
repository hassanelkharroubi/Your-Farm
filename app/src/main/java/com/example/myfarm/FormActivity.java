package com.example.myfarm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfarm.config.Network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
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
    private Button mSumbitBtn;
    private Button mResetButton;
    private TextView mConseilText;
    private TextView mNameText;
    private ImageView mFruitImage;
    private static final String TAG="FormActivity";
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
        mRainText=findViewById(R.id.Rainful);
        mSumbitBtn=findViewById(R.id.send_data);
        mSumbitBtn.setOnClickListener(this);
        mResetButton = findViewById(R.id.resetButton);
        mResetButton.setOnClickListener(this);
        mConseilText=findViewById(R.id.conseil);
        mNameText=findViewById(R.id.name);
        mConseilText.setVisibility(View.GONE);
        mNameText.setVisibility(View.GONE);
        mFruitImage=findViewById(R.id.fruitImage);

    }
    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.resetButton){
            mNitrigenText.setText("");
            mPhosohoText.setText("");
            mPotText.setText("");
            mPHText.setText("");
            mRainText.setText("");
            mTempText.setText("");
            mHumText.setText("");
        }
        int id=view.getId();
        if (id==R.id.send_data){
            String nitrogenText = mNitrigenText.getText().toString();
            String phosphorousText = mPhosohoText.getText().toString();
            String potassiumText = mPotText.getText().toString();
            String pHLevelText = mPHText.getText().toString();
            String rainfallText = mRainText.getText().toString();
            String temperatureText = mTempText.getText().toString();
            String humidityText = mHumText.getText().toString();
            // Check if any field is empty or null
            if (nitrogenText.isEmpty() || phosphorousText.isEmpty() || potassiumText.isEmpty() ||
                    pHLevelText.isEmpty() || rainfallText.isEmpty() || temperatureText.isEmpty() || humidityText.isEmpty()) {
                // Handle empty fields
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return; // Exit the function if any field is empty
            }

            // Convert values to float
            float nitrogenValue = Float.parseFloat(nitrogenText);
            float phosphorousValue = Float.parseFloat(phosphorousText);
            float potassiumValue = Float.parseFloat(potassiumText);
            float pHLevelValue = Float.parseFloat(pHLevelText);
            float rainfallValue = Float.parseFloat(rainfallText);
            float temperatureValue = Float.parseFloat(temperatureText);
            float humidityValue = Float.parseFloat(humidityText);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSumbitBtn.getWindowToken(), 0);


            sendFormValues(nitrogenValue, phosphorousValue, potassiumValue, pHLevelValue, rainfallValue, temperatureValue, humidityValue);


        }
    }
    private void sendFormValues(float nitrogen, float phosphorous, float potassium, float pHLevel, float rainfall, float temperature, float humidity) {
        // Create OkHttp client
        OkHttpClient client = new OkHttpClient();

        // Build the request body
        RequestBody requestBody = new FormBody.Builder()
                .add("nitrogen", String.valueOf(nitrogen))
                .add("phosphorous", String.valueOf(phosphorous))
                .add("potassium", String.valueOf(potassium))
                .add("pHLevel", String.valueOf(pHLevel))
                .add("rainfall", String.valueOf(rainfall))
                .add("temperature", String.valueOf(temperature))
                .add("humidity", String.valueOf(humidity))
                .build();

        // Create the request
        Request request = new Request.Builder()
                .url(Network.getEndApi("soule"))
                .post(requestBody)
                .build();

        // Execute the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Handle success
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String name=jsonObject.getString("prediction");
                        String image_name=jsonObject.getString("image_name");
                        String desc=jsonObject.getString("description");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mConseilText.setVisibility(View.VISIBLE);
                                mConseilText.setText(desc);
                                mNameText.setVisibility(View.VISIBLE);
                                mNameText.setText(name.toUpperCase());
                            }
                        });

                        getImageFromAPI(image_name);

                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                    }

                    // Process the response data
                } else {
                    // Handle unsuccessful response
                    String errorMessage = response.message();
                    // Process the error message
                }
            }
        });
    }
    public void getImageFromAPI(String imageName) {
        OkHttpClient client = new OkHttpClient();

        // Build the URL for the API endpoint with the image name as a query parameter
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Network.getEndApi("get_image")).newBuilder();
        urlBuilder.addQueryParameter("image_name", imageName);
        String apiUrl = urlBuilder.build().toString();

        // Create the request
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        // Send the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Check if the response was successful
                if (response.isSuccessful()) {
                    // Get the image bitmap
                    Bitmap imageBitmap = BitmapFactory.decodeStream(response.body().byteStream());

                    // Update the UI with the image
                    runOnUiThread(() -> {
                        // Display the image in the UI
                        mFruitImage.setImageBitmap(imageBitmap);
                    });
                } else {
                    // Handle unsuccessful response
                    String errorMessage = response.message();
                    Log.i(TAG, errorMessage);
                }
            }
        });
    }


}