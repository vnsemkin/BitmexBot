package bitmex.bitmexspring.services;

import bitmex.bitmexspring.controllers.authorization.APIAuthDataService;
import bitmex.bitmexspring.controllers.json.JsonController;
import bitmex.bitmexspring.models.bitmex.APIAuthData;
import bitmex.bitmexspring.models.bitmex.ClientData;
import bitmex.bitmexspring.models.bitmex.WSAuth;
import bitmex.bitmexspring.models.bitmex.WSRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class BotService {
    private BitmexBot bitmexBot;
    private final ApplicationContext context;
    private final List<BitmexBot> botList;
    private final JsonController json;
    private static final String WS_URL = "wss://ws.testnet.bitmex.com/realtime";
    private APIAuthData wsAuthData;
    private ClientData clientData;
    private final WSHandler wsHandler;
    private WebSocketSession session;

    @Autowired
    public BotService(ApplicationContext context, JsonController json, WSHandler wsHandler) {
        this.context = context;
        this.json = json;
        this.wsHandler = wsHandler;
        botList = new CopyOnWriteArrayList<>();
    }

    public List<BitmexBot> getBotList() {
        return botList;
    }

    public List<BitmexBot> call(ClientData clientData) {
        this.clientData = clientData;
        if (botList.size() == 0) {
            wsStart();
        }
        createBot();
        return botList;
    }

    public void deleteBot(int id){
        for (BitmexBot bot:botList) {
            if (id == bot.getId()) {
                //have to close all bot orders
                bot.cancelOrders();
                bot.getExecutor().shutdown();
                botList.remove(bot);
            }
        }
    }

    private void stopBot(){

    }

    private void createBot() {
        //Each time I need a new instance of BitmexBot class
        bitmexBot = context.getBean(BitmexBot.class);
        bitmexBot.setId(getBotId());
        bitmexBot.setClientData(clientData);
        botList.add(bitmexBot);
        startBot();
    }

    private int getBotId() {
        return botList.stream()
                .mapToInt(BitmexBot::getId)
                .max()
                .orElse(0) + 1;
    }

    public void startBot() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        bitmexBot.setExecutor(executorService);
        executorService.execute(bitmexBot);
    }

    private void wsStart() {
        try {
            wsAuthData = new APIAuthDataService().getAPIAutDataWS(clientData.getKey(), clientData.getSecret());
            wsHandler.setClientData(clientData);
            session = new StandardWebSocketClient().execute(wsHandler, WS_URL).get();
            Thread.sleep(500);
            //Try to authorize
            session.sendMessage(new TextMessage(json
                    .writeToString(new WSAuth(List.of(wsAuthData.getApiKey(),
                            wsAuthData.getApiExpires(),
                            wsAuthData.getApiSignature())))));
            Thread.sleep(500);
//            ping();
            String command = "subscribe";
            session.sendMessage(new TextMessage(json
                    .writeToString(new WSRequest(command, List.of("order")))));
            Thread.sleep(500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}