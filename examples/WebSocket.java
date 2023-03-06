package bitmex.bitmexspring.trash;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocket extends Endpoint {
    private String url;
    private Session session;
    private StringBuffer output;

    public WebSocket(String url, StringBuffer output) {
        super();
        this.url = url;
        this.output = output;
    }

    public void connect() throws URISyntaxException, IOException, DeploymentException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        ClientEndpointConfig config = ClientEndpointConfig.Builder.create()
                .configurator(new ClientEndpointConfig.Configurator())
                .build();
        container.connectToServer(this, config, new URI(url));

    }

    public void send_message(String message) {
        session.getAsyncRemote().sendText(message);
    }


    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                output.setLength(0);
                output.append(message);
            }
        });
    }

    public void onError(Session session, Throwable throwable) {
        super.onError(session, throwable);
    }


    public boolean isOpen() {
        return session.isOpen();
    }
}
