import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class NotepadWebSocketServer extends WebSocketServer {

    private Notepad notepad;

    public NotepadWebSocketServer(Notepad notepad, int port) {
        super(new InetSocketAddress(port));
        this.notepad = notepad;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Connection opened.");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed.");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Forward the message to Notepad
        System.out.println("Received from HTML:" + message);
        notepad.appendTextFromHTML(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started successfully");
    }
}
