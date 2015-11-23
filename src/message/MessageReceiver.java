package message;

import java.net.Socket;

public class MessageReceiver extends Thread {
    private Socket socket;

    public MessageReceiver(Socket socket) {
        this.socket = socket;
    }
}