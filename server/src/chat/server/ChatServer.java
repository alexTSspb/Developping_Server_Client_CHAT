package chat.server;

import chat.network.TCPConnection;
import chat.network.TCPListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPListener {//сделаем слушателем

    public static void main(String[] args) {

        new ChatServer();
    }
    //Список соединений
    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server running ...");
        try (ServerSocket serverSocket = new ServerSocket(8189)){
            while (true)
            {
                try {
                    new TCPConnection(this, serverSocket.accept());//accept ждет нового соединения
                    // как только установитсья возвращает соединение и сразу передаем в конструктор TCPConnection
                }catch(IOException e)
                {
                    System.out.println("Connection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e); //Кроним приложение
        }

    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {

        connections.add(tcpConnection);
        sendToAllConnections("Client connected" + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {

        connections.remove(tcpConnection);
        sendToAllConnections("Client disconnected" + tcpConnection);

    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCP exception "+ e);
    }

    private void sendToAllConnections(String value)
    {
        System.out.println(value);
        for(int i = 0; i < connections.size();i++)
            connections.get(i).sendString(value);


    }
}
