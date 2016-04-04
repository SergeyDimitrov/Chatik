package com.example.pb.chatik;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatService extends IntentService {

    public static final String IP_ADDRESS_KEY = "ip_address_key";
    public static final String PORT_KEY = "port_key";
    public static final String NICKNAME_KEY = "nickname_key";

    public static final String ACTION_CONNECTION_ESTABLISHED = "action_connection_established";
    public static final String ACTION_CONNECTION_FAILED = "action_connection_failed";
    public static final String ACTION_NICKNAME_EXISTS = "action_nickname_exists";
    public static final String ACTION_ERROR = "action_error";

    public static final String SERVER_RESPONSE_OK = "1";
    public static final String SERVER_RESPONSE_NICKNAME_EXISTS = "2";

    public static final int MESSAGE_BUFFER_SIZE = 4096;

    private Socket chatSocket;

    public ChatService() {
        super("ChatService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String ipAddress = intent.getExtras().getString(IP_ADDRESS_KEY);
        int port = intent.getExtras().getInt(PORT_KEY);
        String nickname = intent.getExtras().getString(NICKNAME_KEY);
        try {
            SocketData.chatSocket = chatSocket = new Socket(ipAddress, port);
        } catch (Exception e) {
            sendCast(ACTION_CONNECTION_FAILED);
            return;
        }

        if (!checkNickname(nickname)) {
            return;
        }

        sendCast(ACTION_CONNECTION_ESTABLISHED);

        try {
            BufferedReader messageReader = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
            int bytesRead;
            char[] messageBuffer = new char[MESSAGE_BUFFER_SIZE];
            while ((bytesRead = messageReader.read(messageBuffer)) >= 0) {
                MessageLab.addMessage(String.valueOf(messageBuffer, 0, bytesRead));
            }
        } catch (Exception e) {
            sendCast(ACTION_ERROR);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            chatSocket.close();
        } catch (Exception e) {
            sendCast(ACTION_ERROR);
        }
    }

    private void sendCast(String action) {
        sendBroadcast(new Intent().setAction(action));
    }

    private boolean checkNickname(String nickname) {
        try {
            PrintWriter writer = new PrintWriter(chatSocket.getOutputStream());
            writer.print(nickname);
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
            int bytesRead;
            char[] messageBuffer = new char[MESSAGE_BUFFER_SIZE];
            while ((bytesRead = reader.read(messageBuffer)) == 0);
            String response = String.valueOf(messageBuffer, 0, bytesRead);
            switch (response) {
                case SERVER_RESPONSE_OK:
                    UserData.nickname = nickname;
                    return true;
                case SERVER_RESPONSE_NICKNAME_EXISTS:
                    sendCast(ACTION_NICKNAME_EXISTS);
                    return false;
                default:
                    sendCast(ACTION_ERROR);
                    return false;
            }
        } catch (Exception e) {
            sendCast(ACTION_ERROR);
        }
        return false;
    }

}
