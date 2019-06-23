package chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

//Основной класс нашего соединения - Одного Соединения
public class TCPConnection {
    //
    private final Socket socket;
    //Поток, который будет слушать входящие соединения
    private final Thread rxThread;
    // Потоки ввода вывода
    private final BufferedWriter out;
    private final BufferedReader in;
    private TCPListener tcpListener;

    public TCPConnection(TCPListener tcpListener, String ipAdr, int port) throws IOException
    {
        //Конструктор, который сам создает исходя из адреса и порта
        this(tcpListener, new Socket(ipAdr,port));
    }

    public TCPConnection(TCPListener tcpListener, Socket socket) throws IOException {
        this.socket = socket;
        this.tcpListener = tcpListener;// тот кто создает соединение пусть себя и передаст
        //Далее у сокета получить входящий и исходящий поток
        in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //посложнеее чем просто чтение побайтово
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tcpListener.onConnectionReady(TCPConnection.this);
                    //когда стартовал поток, все готово и передаем себя
                    //Слушаем входящее соединение, считываем строку
                    while(!rxThread.isInterrupted()){//Пока не прерван поток
                        String msg = in.readLine();
                        tcpListener.onReceiveString(TCPConnection.this, msg);
                    }

                } catch (IOException e) {
                    tcpListener.onException(TCPConnection.this, e);

                }finally {
                    tcpListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();

        socket.getOutputStream();
    }
    @Override
    public String toString()
    {

        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

    //Должны быть 2 метода отправка сообщения и прервать соединение
    public void sendString(String value)
    {
        try {
            out.write(value + "\r\n");// Лежит буфер ДОБАВИТЬ КОНЕЦ СТРОКИ
            out.flush();// Освободим буфер
        } catch (IOException e) {
            tcpListener.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect()
    {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            tcpListener.onException(TCPConnection.this,e);
        }
    }
}
