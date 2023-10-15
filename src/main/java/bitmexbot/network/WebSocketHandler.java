package bitmexbot.network;

import bitmexbot.config.BotConstants;
import bitmexbot.entity.BotEntity;
import bitmexbot.entity.BotOrderEntity;
import bitmexbot.model.WSOrderStatus;
import bitmexbot.service.repo.BotRepoService;
import bitmexbot.service.repo.OrderRepoService;
import bitmexbot.service.OrderHandler;
import bitmexbot.util.json.JsonParser;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Objects;

import static bitmexbot.config.BotConstants.*;

@Service
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private final StringBuilder message;
    private final JsonParser json;
    private WSOrderStatus wsOrderStatus;
    private final OrderHandler orderHandler;
    private WebSocketSession session;
    private final BotRepoService botRepoService;
    private final OrderRepoService orderRepoService;
    private boolean messageReceived;
    private final ThreadPoolTaskScheduler taskScheduler;

    public WebSocketHandler(PingTaskScheduler taskScheduler,
                            JsonParser json
            , OrderHandler orderHandler
            , BotRepoService botRepoService
            , OrderRepoService orderRepoService) {
        this.taskScheduler = taskScheduler.getTaskScheduler();
        this.botRepoService = botRepoService;
        this.orderRepoService = orderRepoService;
        this.taskScheduler.initialize();
        this.json = json;
        this.orderHandler = orderHandler;
        this.message = new StringBuilder();
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        try {
            super.afterConnectionEstablished(session);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        this.session = session;
        schedulePingTask();
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session,
                                      @NonNull CloseStatus status) {
        try {
            super.afterConnectionClosed(session, status);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        this.session = null;
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session
            , TextMessage textMessage) {
        message.append(textMessage.getPayload());
        if (!isMessagePong() && json.isTable(message.toString())) {
            onMessagePingDelay();
            wsOrderStatus = (WSOrderStatus) json.readToObject(String.valueOf(message), WSOrderStatus.class);
            if (!Objects.equals(wsOrderStatus.getAction(), PARTIAL)) {
                wsOrderStatus.getBotOrderEntity().forEach(ord -> log.info(wsOrderStatus.getAction()
                        + ": Side " + ord.getSide()
                        + ", Status " + ord.getOrdStatus()
                        + ", id: " + ord.getId()
                        + ", price" + ord.getPrice()));
                for (int i = 0; i < wsOrderStatus.getBotOrderEntity().size(); i++) {
                    // Get Filled Buy order
                    BotOrderEntity botOrderEntity = wsOrderStatus.getBotOrderEntity().get(i);
                    String side = botOrderEntity.getSide();
                    String status = botOrderEntity.getOrdStatus();
                    String orderId = botOrderEntity.getOrderId();
                    BotOrderEntity order = orderRepoService.findByOrderId(orderId);
                    BotEntity botEntityByBitmexOrder = botRepoService.findBotByBitmexOrder(orderId);


                    if (side.equalsIgnoreCase(BUY)
                            && status.equalsIgnoreCase(FILLED)) {
                        order.setFilledPrice(botOrderEntity.getFilledPrice());
                        order.setOrdStatus(FILLED);
                        // Put new order to Bot and save Bot to DB
                        BotOrderEntity newbitmexOrder = orderHandler.sell(botEntityByBitmexOrder, order);
                        botEntityByBitmexOrder.getBotOrderEntities().add(newbitmexOrder);
                        botRepoService.updateBot(botEntityByBitmexOrder);
                    } else if (side.equalsIgnoreCase(BotConstants.SELL)
                            && status.equalsIgnoreCase(BotConstants.FILLED)) {
                        order.setFilledPrice(botOrderEntity.getFilledPrice());
                        order.setOrdStatus(FILLED);
                        BotOrderEntity newbitmexOrder = orderHandler.buy(botEntityByBitmexOrder, order);
                        botEntityByBitmexOrder.getBotOrderEntities().add(newbitmexOrder);
                        botRepoService.updateBot(botEntityByBitmexOrder);

                    } else if (status.equalsIgnoreCase(BotConstants.CANCELED)) {
                        order.setOrdStatus(CANCELED);
                    }
                }
            }
        }
        message.setLength(0);
    }

    private boolean isMessagePong() {
        return message.toString().contentEquals(BotConstants.PONG);
    }

    private void sendPingMessage() {
        if (session != null && !messageReceived) {
            try {
                session.sendMessage(new TextMessage(BotConstants.PING));
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
    }

    private void onMessagePingDelay() {
        if (!messageReceived) {
            //Start ping update timer
            new Thread(this::onMessagePingDelay).start();
        }
        messageReceived = true;
        try {
            Thread.sleep(4500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        messageReceived = false;
    }

    private void schedulePingTask() {
        // 5 seconds
        long pingInterval = 5000;
        taskScheduler.scheduleAtFixedRate(this::sendPingMessage, pingInterval);
    }
}
