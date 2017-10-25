package com.sheremetov.portwatcher;

import com.sheremetov.tcp.Server;
import com.sheremetov.tcp.ServerRequestListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


public class TrayApplication {

    private final PopupMenu popup = new PopupMenu();
    private final SystemTray tray = SystemTray.getSystemTray();
    private TrayIcon trayIconNorm;
    private TrayIcon trayIconWarn;

    private String lastMessage;

    private TrayApplication() {}

    private void start() {
        // check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            System.exit(1);
        }

        initPopupMenu();
        setTrayIconNorm();
        startServer();
    }

    private void initPopupMenu() {
        // Init icons
        trayIconNorm = new TrayIcon(createImage(new File("icon_normal.gif"), "Port Watcher"));
        trayIconWarn = new TrayIcon(createImage(new File("icon_warning.gif"), "External connect detected"));

        // Create a popup menu components
        MenuItem aboutItem = new MenuItem("About");
        MenuItem exitItem = new MenuItem("Exit");

        //Add components to popup menu
        popup.add(aboutItem);
        popup.add(exitItem);

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayInfo("Feel free contact with me by denis" + "." + "sheremetov" + "@" + "gmail.com");
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIconNorm);
                System.exit(0);
            }
        });

        ActionListener iconAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (lastMessage != null) {
                    displayWarning(lastMessage);
                } else {
                    displayInfo("No connections detected");
                }
            }
        };

        trayIconWarn.addActionListener(iconAction);
        trayIconNorm.addActionListener(iconAction);
    }

    private void displayWarning(final String msg) {
        JOptionPane.showMessageDialog(null, msg, "External connect detected", JOptionPane.WARNING_MESSAGE);
    }

    private void displayInfo(final String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    private void setTrayIconNorm() {
        resetTrayMenu();
        trayIconNorm.setPopupMenu(popup);
        try {
            tray.add(trayIconNorm);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            System.exit(1);
        }
    }

    private void setTrayIconWarn() {
        resetTrayMenu();
        trayIconWarn.setPopupMenu(popup);
        try {
            tray.add(trayIconWarn);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            System.exit(1);
        }
    }

    private void resetTrayMenu() {
        tray.remove(trayIconNorm);
        tray.remove(trayIconWarn);
        trayIconNorm.setPopupMenu(null);
        trayIconWarn.setPopupMenu(null);
    }

    private void startServer() {
        Thread thread = new Thread(){
            public void run(){
            try {
                Server server = new Server(6789);
                server.addListener(new ServerRequestListener() {
                    public void handle(String ip, String body) {
                        setTrayIconWarn();
                        lastMessage = String.format("IP: %s\n%s", ip, body);
                        displayWarning(lastMessage);
                    }
                });
                server.run();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            }
        };
        thread.start();
    }

    private static Image createImage(File path, String description) {
        try {
            if (path.isFile()) {
                URL imageURL = new URL("file:///" + path.getAbsolutePath());
                return (new ImageIcon(imageURL, description)).getImage();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws InstantiationException {
        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        UIManager.put("swing.boldMetal", Boolean.FALSE);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TrayApplication app = new TrayApplication();
                app.start();
            }
        });
    }
}