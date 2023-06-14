package com.example.myfarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mNitrigenText;
    private EditText mPhosohoText;
    private EditText mPotText;
    private EditText mPHText;
    private EditText mRainText;
    private EditText mTempText;
    private EditText mHumText;

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
}