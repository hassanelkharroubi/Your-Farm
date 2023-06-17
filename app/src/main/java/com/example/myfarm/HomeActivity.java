package com.example.myfarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TaskInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    
    private Button mBotbtn;
    private Button mFertilisation;
    private Button mDanger;
    private Button meteo;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBotbtn=findViewById(R.id.assistant);
        mBotbtn.setOnClickListener(this);

        mFertilisation=findViewById(R.id.fertilisation);
        mFertilisation.setOnClickListener(this);

        mDanger=findViewById(R.id.danger);
        mDanger.setOnClickListener(this);

        meteo=findViewById(R.id.meteo);
        meteo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        if (id==R.id.assistant)
            startActivity(new Intent(HomeActivity.this, ChatActivity.class));

        if (id==R.id.fertilisation)
            startActivity(new Intent(HomeActivity.this, FormActivity.class));

        if (id==R.id.danger)
            //Toast.makeText(this, "Feature is Comming Soon", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        if (id==R.id.meteo)
            Toast.makeText(this, "Feature is Comming Soon", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(HomeActivity.this, ChatActivity.class));


    }


}