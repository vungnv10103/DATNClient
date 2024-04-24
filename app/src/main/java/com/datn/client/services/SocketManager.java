package com.datn.client.services;

import android.content.Context;

import com.datn.client.ui.components.MyDialog;
import com.datn.client.utils.Constants;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    private static SocketManager instance;
    private Socket mSocket;

    private SocketManager(Context context) {
        try {
            mSocket = IO.socket(Constants.URL_API);
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
            MyDialog.gI().startDlgOK(context, e);
        }
    }

    public static synchronized SocketManager getInstance(Context context) {
        if (instance == null) {
            instance = new SocketManager(context);
        }
        return instance;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void connect() {
        if (mSocket != null && !mSocket.connected()) {
            mSocket.connect();
        }
    }

    public void disconnect() {
        if (mSocket != null && mSocket.connected()) {
            mSocket.disconnect();
        }
    }

    public void close() {
        if (mSocket != null) {
            mSocket.close();
        }
    }
}
