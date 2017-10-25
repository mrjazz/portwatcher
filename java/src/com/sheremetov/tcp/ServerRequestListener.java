package com.sheremetov.tcp;


public interface ServerRequestListener {

    void handle(String ip, String body);

}
