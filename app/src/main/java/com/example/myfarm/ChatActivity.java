package com.example.myfarm;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfarm.adapter.ChatAdapter;
import com.example.myfarm.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;


public class ChatActivity extends AppCompatActivity {

    private static final String TAG ="ChatActivity" ;
    private RecyclerView recyclerView;
    private EditText inputEditText;
    private Button sendButton;

    private ChatAdapter chatAdapter;
    private List<Message> dataList ;

    private static final String END_GET_API="http://192.168.8.100:5000/chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        dataList= new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);

        // Set up RecyclerView adapter
        chatAdapter = new ChatAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = inputEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            // Add the user message to the chat adapter
            chatAdapter.addMessage(new Message(message, "user"));
            // Clear the input field
            inputEditText.setText("");
            // Send the message to the API and handle the response
            // You'll implement this in later steps
            sendToAPI(message);
        }
    }



    public void sendToAPI(String messageContent) {
        OkHttpClient client = new OkHttpClient();

        // Create the request body
        RequestBody requestBody = new FormBody.Builder()
                .add("message", messageContent)
                .build();
        Log.i(TAG,"message to send is "+messageContent);

        // Create the request
        Request request = new Request.Builder()
                .url(END_GET_API)
                .post(requestBody)
                .build();

        // Send the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the failure
                e.printStackTrace();
                Log.e(TAG,e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle the response
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Message chatMessage = new Message(responseData, "bot");
                    Log.i(TAG,"from bot :"+ chatMessage.getContent());

                    // Add the new ChatMessage to your data list


                    // Notify the adapter that the data set has changed
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dataList.add(chatMessage);
                            chatAdapter.addMessage(chatMessage);
                            chatAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    // Handle unsuccessful response
                    String errorMessage = response.message();
                    Log.e(TAG,"Error mesage "+errorMessage);
                    // Handle the error message
                }
            }
        });
    }

}
