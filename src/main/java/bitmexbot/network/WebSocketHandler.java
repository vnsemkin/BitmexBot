package bitmexbot.network;

import bitmexbot.config.BitmexConstants;
import bitmexbot.entity.BitmexBot;
import bitmexbot.entity.BitmexOrder;
import bitmexbot.model.WSOrderStatus;
import bitmexbot.repository.BotRepo;
import bitmexbot.repository.OrderRepo;
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
import java.util.Optional;

import static bitmexbot.config.BitmexConstants.*;

@Service
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private final StringBuilder message;
    private final JsonParser json;
    private WSOrderStatus wsOrderStatus;
    private final OrderHandler orderHandler;
    private WebSocketSession session;
    private final BotRepo botRepo;
    private final OrderRepo orderRepo;
    private boolean messageReceived;
    private final ThreadPoolTaskScheduler taskScheduler;

    public WebSocketHandler(PingTaskScheduler taskScheduler,
                            JsonParser json
            , OrderHandler orderHandler
            , BotRepo botRepo
            , OrderRepo orderRepo) {
        this.taskScheduler = taskScheduler.getTaskScheduler();
        this.botRepo = botRepo;
        this.orderRepo = orderRepo;
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
            e.printStackTrace();
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
            e.printStackTrace();
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
                wsOrderStatus.getBitmexOrder().forEach(ord -> log.info(wsOrderStatus.getAction()
                        + ": Side " + ord.getSide()
                        + ", Status " + ord.getOrdStatus()
                        + ", id: " + ord.getId()
                        + ", price" + ord.getPrice()));
                for (int i = 0; i < wsOrderStatus.getBitmexOrder().size(); i++) {
                    // Get Filled Buy order
                    BitmexOrder bitmexOrder = wsOrderStatus.getBitmexOrder().get(i);
                    String side = bitmexOrder.getSide();
                    String status = bitmexOrder.getOrdStatus();
                    String orderId = bitmexOrder.getOrderId();
                    BitmexOrder order = orderRepo.findByOrderId(orderId);
                    Optional<BitmexBot> botByBitmexOrder = botRepo.findBotByBitmexOrder(orderId);
                    BitmexBot updatedBot = botByBitmexOrder.orElseGet(BitmexBot::new);

                    if (side.equalsIgnoreCase(BUY)
                            && status.equalsIgnoreCase(FILLED)) {
                        order.setFilledPrice(bitmexOrder.getFilledPrice());
                        order.setOrdStatus(FILLED);
                        // Put new order to Bot and save Bot to DB
                        BitmexOrder newbitmexOrder = orderHandler.sell(updatedBot, order);
                        updatedBot.getBitmexOrders().add(newbitmexOrder);
                        botRepo.updateBot(updatedBot);
                    } else if (side.equalsIgnoreCase(BitmexConstants.SELL)
                            && status.equalsIgnoreCase(BitmexConstants.FILLED)) {
                        order.setFilledPrice(bitmexOrder.getFilledPrice());
                        order.setOrdStatus(FILLED);
                        BitmexOrder newbitmexOrder = orderHandler.buy(updatedBot, order);
                        updatedBot.getBitmexOrders().add(newbitmexOrder);
                        botRepo.updateBot(updatedBot);

                    } else if (status.equalsIgnoreCase(BitmexConstants.CANCELED)) {
                        order.setOrdStatus(CANCELED);
                    }
                }
            }
        }
        message.setLength(0);
    }

    private boolean isMessagePong() {
        return message.toString().contentEquals(BitmexConstants.PONG);
    }

    private void sendPingMessage() {
        if (session != null && !messageReceived) {
            try {
                session.sendMessage(new TextMessage(BitmexConstants.PING));
            } catch (IOException e) {
                e.printStackTrace();
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
