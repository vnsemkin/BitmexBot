package bitmexbot.service;

import bitmexbot.aspect.BitmexLog;
import bitmexbot.config.BitmexConstants;
import bitmexbot.entity.BitmexBot;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.entity.BitmexOrder;
import bitmexbot.model.APIAuthData;
import bitmexbot.model.WSAuth;
import bitmexbot.model.WSRequest;
import bitmexbot.network.WebSocketHandler;
import bitmexbot.repository.BotRepo;
import bitmexbot.util.authorization.APIAuthDataService;
import bitmexbot.util.json.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BotFactory {
    @Value("${bitmex.websocket.url}")
    private static String WS_URL;
    private final OrderHandler orderHandler;
    private final JsonParser json;
    private final WebSocketHandler webSocketHandler;
    private final BotRepo botRepo;

    public BotFactory(OrderHandler orderHandler
            , JsonParser json
            , WebSocketHandler webSocketHandler
            , BotRepo botRepo) {
        this.orderHandler = orderHandler;
        this.json = json;
        this.webSocketHandler = webSocketHandler;
        this.botRepo = botRepo;
    }

    public List<BitmexBot> createNewBot(BitmexBotData bitmexBotData) {
        List<BitmexBot> botList = botRepo.findAll();
        if (botList.isEmpty()) {
            wsStart(bitmexBotData);
        }
        return createNewBot(bitmexBotData, botList);
    }

    public List<BitmexBot> deleteBot(int id) {
        botRepo.deleteByBotId(id);
        return botRepo.findAll();
    }

    private List<BitmexBot> createNewBot(BitmexBotData bitmexBotData
            , List<BitmexBot> botList) {
        BitmexBot bitmexBot = new BitmexBot();
        Set<BitmexOrder> bitmexOrders = new HashSet<>();
        bitmexBot.setBotId(getBotId(botList));
        bitmexBot.setBitmexBotData(bitmexBotData);
        bitmexBot.setBitmexOrders(bitmexOrders);
        //Put bot to DB
        BitmexBot bot = botRepo.createBot(bitmexBot);
        // Add bot to bot list
        startBot(bot);
        return botRepo.findAll();
    }

    private int getBotId(List<BitmexBot> botList) {
        return botList.stream()
                .mapToInt(BitmexBot::getBotId)
                .max()
                .orElse(0) + 1;
    }

    @BitmexLog(message = "New Bot Started")
    public void startBot(BitmexBot bitmexBot) {
        orderHandler.initialBuy(bitmexBot);
    }

    @BitmexLog(message = "WebSocket started")
    private void wsStart(BitmexBotData bitmexBotData) {
        try {
            APIAuthData wsAuthData = new APIAuthDataService().getAPIAutDataWS(bitmexBotData.getKey()
                    , bitmexBotData.getSecret());
            WebSocketSession session = new StandardWebSocketClient()
                    .execute(webSocketHandler, WS_URL)
                    .get();
            Thread.sleep(BitmexConstants.TIMEOUT_500);
            session.sendMessage(new TextMessage(json
                    .writeToString(new WSAuth(List.of(wsAuthData.getApiKey(),
                            wsAuthData.getApiExpires(),
                            wsAuthData.getApiSignature())))));
            Thread.sleep(BitmexConstants.TIMEOUT_500);
            String command = BitmexConstants.SUBSCRIBE;
            session.sendMessage(new TextMessage(json
                    .writeToString(new WSRequest(command, List.of(BitmexConstants.ORDER)))));
            Thread.sleep(BitmexConstants.TIMEOUT_500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}