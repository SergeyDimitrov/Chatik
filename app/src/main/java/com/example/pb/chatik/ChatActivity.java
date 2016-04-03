package com.example.pb.chatik;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {


    private ListView chatListView;
    private EditText messageEditText;
    private Button sendMessageButton;

    private BroadcastReceiver connectionStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(ChatActivity.this, R.string.server_connection_lost, Toast.LENGTH_LONG).show();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatListView = (ListView) findViewById(R.id.chat_view);
        chatListView.setAdapter(MessageLab.getInstance(this));
        MessageLab.setHandler(new Handler());

        messageEditText = (EditText) findViewById(R.id.message_edit_text);
        sendMessageButton = (Button) findViewById(R.id.send_message_button);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageLab.sendMessage(messageEditText.getText().toString());
                messageEditText.setText("");
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ChatService.ACTION_ERROR);
        registerReceiver(connectionStateReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(connectionStateReceiver);
    }
}
