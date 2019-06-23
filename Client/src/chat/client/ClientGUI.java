package chat.client;

import chat.network.TCPConnection;
import chat.network.TCPListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientGUI extends JFrame implements ActionListener, TCPListener {
//Временно сделано на SWINg
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;
    private static final String IP_ADDR = "localhost";
    private static final int PORT = 8189;

    private final JTextArea log = new JTextArea();
    private final JTextField fieldName = new JTextField();
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientGUI();
            }
        });
    }
    private ClientGUI()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);

        fieldInput.addActionListener(this);
       //не используем анонимный класс
        add(log, BorderLayout.CENTER);
        add(fieldInput,BorderLayout.SOUTH);
        add(fieldName,BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldName.getText() + " " + msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMSG("Connection ready/...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMSG(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMSG("connection close///");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMSG("connecion exception ");
    }

    private synchronized void printMSG(String msg)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
