package bitmexbot.service;

import bitmexbot.aspect.Logging;
import bitmexbot.config.BotConstants;
import bitmexbot.entity.BotEntity;
import bitmexbot.entity.BotDataEntity;
import bitmexbot.entity.BotOrderEntity;
import bitmexbot.exception.BotNotFoundException;
import bitmexbot.model.APIAuthData;
import bitmexbot.model.WSAuth;
import bitmexbot.model.WSRequest;
import bitmexbot.network.WebSocketHandler;
import bitmexbot.service.repo.BotRepoService;
import bitmexbot.util.authorization.APIAuthDataService;
import bitmexbot.util.json.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Slf4j
@Service
public class BotFactory {
    @Value("${bitmex.websocket.url}")
    private String WS_URL;
    private final OrderHandler orderHandler;
    private final JsonParser json;
    private final WebSocketHandler webSocketHandler;
    private final BotRepoService botRepoService;

    public BotFactory(OrderHandler orderHandler
            , JsonParser json
            , WebSocketHandler webSocketHandler
            , BotRepoService botRepoService) {
        this.orderHandler = orderHandler;
        this.json = json;
        this.webSocketHandler = webSocketHandler;
        this.botRepoService = botRepoService;
    }

    public List<BotEntity> createNewBot(BotDataEntity botDataEntity) {
        List<BotEntity> botEntityList = botRepoService.findAll();
        if (botEntityList.isEmpty()) {
            wsStart(botDataEntity);
        }
        return startNewBot(botDataEntity, botEntityList);

    }

    @Logging(message = "Bot was deleted")
    public List<BotEntity> removeBot(int id) {
        BotEntity byBotIdEntity = botRepoService.findByBotId(id);
        if (Objects.nonNull(byBotIdEntity.getId())) {
            orderHandler.delete(byBotIdEntity);
            botRepoService.removeByBotId(id);
        } else {
            throw new BotNotFoundException("Bot with id: " + id + " not found");
        }
        return botRepoService.findAllBotWithOrders();
    }

    private List<BotEntity> startNewBot(BotDataEntity botDataEntity
            , List<BotEntity> botEntityList) {
        BotEntity bitmexBotEntity = new BotEntity();
        Set<BotOrderEntity> botOrderEntities = new HashSet<>();
        bitmexBotEntity.setBotId(getBotId(botEntityList));
        bitmexBotEntity.setBotDataEntity(botDataEntity);
        bitmexBotEntity.setBotOrderEntities(botOrderEntities);
        //Put bot to DB
        BotEntity botEntity = botRepoService.createBot(bitmexBotEntity);
        // Add bot to bot list
        startBot(botEntity);
        return botRepoService.findAll();
    }

    private int getBotId(List<BotEntity> botEntityList) {
        return botEntityList.stream()
                .mapToInt(BotEntity::getBotId)
                .max()
                .orElse(0) + 1;
    }

    public void startBot(BotEntity botEntity) {
        orderHandler.initialBuy(botEntity);
    }

    @Logging(message = "WebSocket Started")
    private void wsStart(BotDataEntity botDataEntity) {
        try {
            APIAuthData wsAuthData = new APIAuthDataService().getAPIAutDataWS(botDataEntity.getKey()
                    , botDataEntity.getSecret());
            WebSocketSession session = new StandardWebSocketClient()
                    .execute(webSocketHandler, WS_URL)
                    .get();
            Thread.sleep(BotConstants.TIMEOUT_500);
            session.sendMessage(new TextMessage(json
                    .writeToString(new WSAuth(List.of(wsAuthData.getApiKey(),
                            wsAuthData.getApiExpires(),
                            wsAuthData.getApiSignature())))));
            Thread.sleep(BotConstants.TIMEOUT_500);
            String command = BotConstants.SUBSCRIBE;
            session.sendMessage(new TextMessage(json
                    .writeToString(new WSRequest(command, List.of(BotConstants.ORDER)))));
            Thread.sleep(BotConstants.TIMEOUT_500);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}