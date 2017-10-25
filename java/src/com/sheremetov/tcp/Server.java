package com.sheremetov.tcp;

import java.io.*;
import java.net.*;
import java.util.LinkedList;


public class Server implements Runnable {

    private final int port;
    private final LinkedList<ServerRequestListener> listeners = new LinkedList<ServerRequestListener>();

    public Server(int port) {
        this.port = port;
    }

    public void addListener(ServerRequestListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeListener(ServerRequestListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String ip, String body) {
        for (ServerRequestListener listener : listeners) {
            listener.handle(ip, body);
        }
    }

    @Override
    public void run() {
        try {
            ServerSocket welcomeSocket = new ServerSocket(port);

            while (true) {
                Socket connectionSocket = welcomeSocket.accept();
                BufferedReader inFromClient =
                        new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                String ipAddress = connectionSocket.getInetAddress().toString();
                StringBuilder requestBody = new StringBuilder();
                while (true) {
                    int b = inFromClient.read();
                    if (b < 0) break;
                    requestBody.append((char) b);
                }

                notifyListeners(ipAddress, requestBody.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
