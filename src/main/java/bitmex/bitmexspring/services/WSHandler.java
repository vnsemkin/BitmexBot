package bitmex.bitmexspring.services;

import bitmex.bitmexspring.config.BitmexConstants;
import bitmex.bitmexspring.controllers.endpoints.IndexController;
import bitmex.bitmexspring.controllers.json.JsonController;
import bitmex.bitmexspring.models.bitmex.ClientData;
import bitmex.bitmexspring.models.bitmex.WSOrderStatus;
import bitmex.bitmexspring.models.user.Order;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class WSHandler extends TextWebSocketHandler {
    private final ApplicationContext context;
    private final StringBuilder message = new StringBuilder();
    private final JsonController json;
    private WSOrderStatus wsOrderStatus;
    private final OrderPost orderPost;
    private ClientData clientData;
    private WebSocketSession session;
    private boolean messageReceived = false;
    private final ThreadPoolTaskScheduler taskScheduler;

    public WSHandler(ApplicationContext context, PingTaskScheduler taskScheduler,
                     JsonController json, OrderPost orderPost) {
        this.context = context;
        this.taskScheduler = taskScheduler.getTaskScheduler();
        this.taskScheduler.initialize();
        this.json = json;
        this.orderPost = orderPost;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
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
    public void handleTextMessage(@NonNull WebSocketSession session, TextMessage textMessage) {
        IndexController indexController = context.getBean(IndexController.class);
        message.append(textMessage.getPayload());
        if (!isMessagePong() && json.isTable(message.toString())) {
            onMessagePingDelay();
            wsOrderStatus = (WSOrderStatus) json.readToObject(String.valueOf(message), WSOrderStatus.class);
            if (!Objects.equals(wsOrderStatus.getAction(), BitmexConstants.PARTIAL)) {
                wsOrderStatus.getOrder().forEach(ord -> log.info(wsOrderStatus.getAction()
                        + ": Side " + ord.getSide()
                        + ", Status " + ord.getOrdStatus()
                        + ", id: " + ord.getId()
                        + ", price" + ord.getPrice()));
                for (int i = 0; i < wsOrderStatus.getOrder().size(); i++) {
                    if (wsOrderStatus.getOrder().get(i).getSide().equals(BitmexConstants.BUY)
                            && wsOrderStatus.getOrder().get(i).getOrdStatus().equals(BitmexConstants.FILLED)) {
                        clientData.setFilledPrice(wsOrderStatus.getOrder().get(i).getPrice());
                        orderPost.setClientData(clientData);
                        Order order = orderPost.sell();
                        orderUpdate(indexController, i, order);
                    } else if (wsOrderStatus.getOrder().get(i).getSide().equals(BitmexConstants.SELL)
                            && wsOrderStatus.getOrder().get(i).getOrdStatus().equals(BitmexConstants.FILLED)) {
                        clientData.setFilledPrice(wsOrderStatus.getOrder().get(i).getPrice());
                        orderPost.setClientData(clientData);
                        Order order = orderPost.buy();
                        orderUpdate(indexController, i, order);
                    } else if (wsOrderStatus.getOrder().get(i).getOrdStatus().equals(BitmexConstants.CANCELED)) {
                        for (BitmexBot bot : indexController.getBotList()) {
                            for (Order ord : bot.getOrderList()) {
                                if (Objects.equals(ord.getId(), wsOrderStatus.getOrder().get(i).getId())) {
                                    bot.getOrderList().remove(ord);
                                }
                            }
                        }
                    }
                }
            }
        }
        message.setLength(0);
    }

    private void orderUpdate(IndexController indexController, int i, Order order) {
        for (BitmexBot bot : indexController.getBotList()) {
            for (Order ord : bot.getOrderList()) {
                if (Objects.equals(ord.getId(), wsOrderStatus.getOrder().get(i).getId())) {
                    bot.getOrderList().remove(ord);
                    bot.getOrderList().add(order);
                }
            }
        }
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
            Thread.currentThread().sleep(4500);
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
