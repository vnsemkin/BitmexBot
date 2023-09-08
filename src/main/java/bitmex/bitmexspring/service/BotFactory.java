package bitmex.bitmexspring.service;

import bitmex.bitmexspring.aspect.BitmexLog;
import bitmex.bitmexspring.config.BitmexConstants;
import bitmex.bitmexspring.entity.BitmexBot;
import bitmex.bitmexspring.entity.BitmexBotData;
import bitmex.bitmexspring.entity.BitmexOrder;
import bitmex.bitmexspring.model.bitmex.APIAuthData;
import bitmex.bitmexspring.model.bitmex.WSAuth;
import bitmex.bitmexspring.model.bitmex.WSRequest;
import bitmex.bitmexspring.repository.jpa.BotRepo;
import bitmex.bitmexspring.util.authorization.APIAuthDataService;
import bitmex.bitmexspring.util.json.JsonParser;
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
    private final PostOrder postOrder;
    private final JsonParser json;
    private final WSHandler wsHandler;
    private final BotRepo botRepo;

    public BotFactory(PostOrder postOrder
            , JsonParser json
            , WSHandler wsHandler
            , BotRepo botRepo) {
        this.postOrder = postOrder;
        this.json = json;
        this.wsHandler = wsHandler;
        this.botRepo = botRepo;
    }

    public List<BitmexBot> createNewBotAndGetExisting(BitmexBotData bitmexBotData) {
        List<BitmexBot> botList = botRepo.findAll();
        if (botList.isEmpty()) {
            wsStart(bitmexBotData);
        }
        return createNewBot(bitmexBotData, botList);
    }

    public void deleteBot(int id) {
        List<BitmexBot> botList = botRepo.findAll();
        for (BitmexBot bot : botList) {
            if (id == bot.getBotId()) {
                postOrder.delete(bot);
                botRepo.deleteByBotId(id);
            }
        }
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
        postOrder.initialBuy(bitmexBot);
    }

    @BitmexLog(message = "WebSocket started")
    private void wsStart(BitmexBotData bitmexBotData) {
        try {
            APIAuthData wsAuthData = new APIAuthDataService().getAPIAutDataWS(bitmexBotData.getKey()
                    , bitmexBotData.getSecret());
            WebSocketSession session = new StandardWebSocketClient()
                    .execute(wsHandler, WS_URL)
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