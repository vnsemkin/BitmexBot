package bitmex.bitmexspring.services;

import bitmex.bitmexspring.controllers.json.JsonController;
import bitmex.bitmexspring.models.bitmex.*;
import bitmex.bitmexspring.controllers.authorization.APIAuthDataService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@PropertySource("classpath:network.properties")
public class BitmexBot implements Runnable {
    private static final String WS_URL = "wss://ws.testnet.bitmex.com/realtime";
    public ClientData clientData;
    private final JsonController json;
    private final WSHandler wsHandler;
    private final WebSocketClient webSocketClient;
    private final OrderPost orderPost;
    private APIAuthData wsAuthData;
    private WebSocketSession session;

    public BitmexBot(WSHandler wsHandler, JsonController json, OrderPost orderPost) {
        this.json = json;
        this.wsHandler = wsHandler;
        this.orderPost = orderPost;
        webSocketClient = new StandardWebSocketClient();
    }

    @Override
    public void run() {
        wsAuthData = new APIAuthDataService().getAPIAutDataWS(clientData.getKey(), clientData.getSecret());
        wsStart();
        orderPost.initialBuy();
        ping();
    }

    private void wsStart(){
        try{
        session = webSocketClient.execute(wsHandler, WS_URL).get();
        Thread.sleep(500);
        //Try to authorize
        session.sendMessage(new TextMessage(json
                .writeToString(new WSAuth(List.of(wsAuthData.getApiKey(),
                        wsAuthData.getApiExpires(),
                        wsAuthData.getApiSignature())))));
        Thread.sleep(500);
            String command = "subscribe";
            session.sendMessage(new TextMessage(json
                .writeToString(new WSRequest(command, List.of("order")))));
        Thread.sleep(500);

    }catch (Exception e){
        e.printStackTrace();
    }
    }

    private void ping(){
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(new WSPing(session));
    }
}

