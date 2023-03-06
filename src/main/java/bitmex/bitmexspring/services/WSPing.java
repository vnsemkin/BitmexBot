package bitmex.bitmexspring.services;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WSPing implements Runnable {
    WebSocketSession session;

    public WSPing(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void run() {
        while (true) {
            try {
                session.sendMessage(new TextMessage("ping"));
                try {
                    Thread.sleep(4500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
