package bitmex.bitmexspring.services;

import bitmex.bitmexspring.aspect.BitmexLog;
import bitmex.bitmexspring.config.BitmexConstants;
import bitmex.bitmexspring.controllers.authorization.APIAuthDataService;
import bitmex.bitmexspring.controllers.json.JsonController;
import bitmex.bitmexspring.models.bitmex.APIAuthData;
import bitmex.bitmexspring.models.bitmex.ClientData;
import bitmex.bitmexspring.models.bitmex.WSAuth;
import bitmex.bitmexspring.models.bitmex.WSRequest;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${bitmex.ws_url}")
    private static String WS_URL;
    private BitmexBot bitmexBot;
    private final ApplicationContext context;
    private final List<BitmexBot> botList;
    private final JsonController json;
    private ClientData clientData;
    private final WSHandler wsHandler;

    public BotService(ApplicationContext context
            , JsonController json
            , WSHandler wsHandler) {
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

    public void deleteBot(int id) {
        for (BitmexBot bot : botList) {
            if (id == bot.getId()) {
                bot.cancelOrders();
                bot.getExecutor().shutdown();
                botList.remove(bot);
            }
        }
    }

    private void createBot() {
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

    @BitmexLog(message = "New Bot Started")
    public void startBot() {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        bitmexBot.setExecutor(executorService);
        executorService.execute(bitmexBot);
    }

    @BitmexLog(message = "WebSocket started")
    private void wsStart() {
        try {
            APIAuthData wsAuthData = new APIAuthDataService().getAPIAutDataWS(clientData.getKey()
                    , clientData.getSecret());
            wsHandler.setClientData(clientData);
            WebSocketSession session = new StandardWebSocketClient().execute(wsHandler, WS_URL).get();
            Thread.sleep(500);
            session.sendMessage(new TextMessage(json
                    .writeToString(new WSAuth(List.of(wsAuthData.getApiKey(),
                            wsAuthData.getApiExpires(),
                            wsAuthData.getApiSignature())))));
            Thread.sleep(500);
            String command = BitmexConstants.SUBSCRIBE;
            session.sendMessage(new TextMessage(json
                    .writeToString(new WSRequest(command, List.of(BitmexConstants.ORDER)))));
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}