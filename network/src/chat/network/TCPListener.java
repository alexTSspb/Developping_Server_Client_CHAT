package chat.network;

public interface TCPListener {
//    События которые могут возникнуть
    void onConnectionReady(TCPConnection tcpConnection); //Передаем экземпляр самого сединения,
    // чтобы у того кто вызывал был доступ Запустили соединения
    void onReceiveString (TCPConnection tcpConnection, String value); // Получить строчку
    void onDisconnect(TCPConnection tcpConnection); // Соединение оборвалось
    void onException(TCPConnection tcpConnection, Exception e); //Чтото пошло не так и случилось исключение

}
