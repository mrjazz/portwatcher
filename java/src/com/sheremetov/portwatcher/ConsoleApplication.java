package com.sheremetov.portwatcher;

import com.sheremetov.tcp.Server;
import com.sheremetov.tcp.ServerRequestListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class ConsoleApplication {

    private static final String SETTINGS_FILE = "settings.ini";

    public static void main(String[] args) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(SETTINGS_FILE));
        } catch (IOException e) {
            System.out.println(String.format("Setting file %s not found", SETTINGS_FILE));
            System.exit(1);
        }

        Server server = new Server(Integer.parseInt(props.getProperty("port")));
        server.addListener(new ServerRequestListener() {
            public void handle(String ip, String body) {
                String msg = String.format("IP: %s\n%s", ip, body);
                EmailSender email = new EmailSender(SETTINGS_FILE);
                email.send(msg);
            }
        });
        server.run();

        System.out.println("PortWatcher started...");
    }

}
