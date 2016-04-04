package com.example.pb.chatik;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConnectActivity extends AppCompatActivity {

    private EditText mIpEditText;
    private EditText mPortEditText;
    private EditText mNicknameEditText;
    private Button mConnectButton;

    private BroadcastReceiver chatServiceCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ChatService.ACTION_CONNECTION_ESTABLISHED:
                    startActivity(new Intent(ConnectActivity.this, ChatActivity.class));
                    break;
                case ChatService.ACTION_CONNECTION_FAILED:
                    Toast.makeText(ConnectActivity.this, R.string.server_connection_error, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        mIpEditText = (EditText) findViewById(R.id.ip_edit_text);
        mPortEditText = (EditText) findViewById(R.id.port_edit_text);
        mNicknameEditText = (EditText) findViewById(R.id.nickname_edit_text);
        mConnectButton = (Button) findViewById(R.id.connect_button);

        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checkNickname()) {
                        startService(getChatServiceIntent());
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(ConnectActivity.this, R.string.port_format_error, Toast.LENGTH_LONG).show();
                }
            }
        });

        IntentFilter chatServiceCastFilter = new IntentFilter();
        chatServiceCastFilter.addAction(ChatService.ACTION_CONNECTION_ESTABLISHED);
        chatServiceCastFilter.addAction(ChatService.ACTION_CONNECTION_FAILED);
        registerReceiver(chatServiceCastReceiver, chatServiceCastFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(chatServiceCastReceiver);
    }

    private Intent getChatServiceIntent() throws NumberFormatException {
        Intent chatServiceIntent = new Intent(ConnectActivity.this, ChatService.class);
        chatServiceIntent.putExtra(ChatService.IP_ADDRESS_KEY, mIpEditText.getText().toString());
        chatServiceIntent.putExtra(ChatService.PORT_KEY, Integer.parseInt(mPortEditText.getText().toString()));
        chatServiceIntent.putExtra(ChatService.NICKNAME_KEY, mNicknameEditText.getText().toString());
        return chatServiceIntent;
    }

    private boolean checkNickname() {
        if (mNicknameEditText.getText() == null
                || mNicknameEditText.getText().toString().equals("")) {
            Toast.makeText(this, R.string.empty_nickname_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
