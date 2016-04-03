package com.example.pb.chatik;

import android.os.AsyncTask;

import java.io.PrintWriter;

public class SendMessageTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        String message = params[0];
        try {
            PrintWriter messageWriter = new PrintWriter(SocketData.chatSocket.getOutputStream());
            messageWriter.print(message);
            messageWriter.flush();
        } catch (Exception e) {

        }
        return null;
    }
}
