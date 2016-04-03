package com.example.pb.chatik;

import android.content.Context;
import android.os.Handler;
import android.widget.ArrayAdapter;


public class MessageLab {
    private static ArrayAdapter<String> chatListAdapter;
    private static Handler handler;

    public static ArrayAdapter<String> getInstance(Context context) {
        if (chatListAdapter == null) {
            return chatListAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        }
        return chatListAdapter;
    }

    public static void setHandler(Handler handler) {
        MessageLab.handler = handler;
    }

    public static void addMessage(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                chatListAdapter.add(message);
                chatListAdapter.notifyDataSetChanged();
            }
        });
    }

    public static void sendMessage(String message) {
        new SendMessageTask().execute(message);
    }
}
