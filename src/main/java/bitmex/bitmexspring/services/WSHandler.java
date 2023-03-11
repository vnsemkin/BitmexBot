package bitmex.bitmexspring.services;

import bitmex.bitmexspring.controllers.endpoints.IndexController;
import bitmex.bitmexspring.controllers.json.JsonController;
import bitmex.bitmexspring.models.bitmex.ClientData;
import bitmex.bitmexspring.models.bitmex.WSOrderStatus;
import bitmex.bitmexspring.models.user.Order;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WSHandler extends TextWebSocketHandler {
    private ApplicationContext context;
    private final StringBuilder message = new StringBuilder();
    private final StringBuilder pong = new StringBuilder("pong");
    private final JsonController json;
    private WSOrderStatus wsOrderStatus;
    private final OrderPost orderPost;
    private ClientData clientData;
    private final long pingInterval = 5000; // 5 seconds
    private WebSocketSession session;
    private boolean messageReceived = false;
    private final ThreadPoolTaskScheduler taskScheduler;

    @Autowired
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
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            super.afterConnectionClosed(session, status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.session = null;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        IndexController indexController = context.getBean(IndexController.class);
        //Get bean for updating indexController botList
        message.append(textMessage.getPayload());
        if (!isMessagePong() && json.isTable(message.toString())) {
            onMessagePingDelay();
            wsOrderStatus = (WSOrderStatus) json.readToObject(String.valueOf(message), WSOrderStatus.class);
            if (!Objects.equals(wsOrderStatus.getAction(), "partial")) {
                //for test
                wsOrderStatus.getOrder().forEach(ord -> System.out.println(wsOrderStatus.getAction()
                        + ": Side " + ord.getSide()
                        + ", Status " + ord.getOrdStatus()
                        + ", id: " + ord.getId()
                        + ", price" + ord.getPrice()));
                //
                for (int i = 0; i < wsOrderStatus.getOrder().size(); i++) {
                    if (wsOrderStatus.getOrder().get(i).getSide().equals("Buy")
                            && wsOrderStatus.getOrder().get(i).getOrdStatus().equals("Filled")) {
                        clientData.setFilledPrice(wsOrderStatus.getOrder().get(i).getPrice());
                        orderPost.setClientData(clientData);
                        Order order = orderPost.sell();
                        orderUpdate(indexController, i, order);
                    } else if (wsOrderStatus.getOrder().get(i).getSide().equals("Sell")
                            && wsOrderStatus.getOrder().get(i).getOrdStatus().equals("Filled")) {
                        clientData.setFilledPrice(wsOrderStatus.getOrder().get(i).getPrice());
                        orderPost.setClientData(clientData);
                        Order order = orderPost.buy();
                        orderUpdate(indexController, i, order);
                    } else if (wsOrderStatus.getOrder().get(i).getOrdStatus().equals("Canceled")) {
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
        return message.toString().equals(pong.toString());
    }

    private void sendPingMessage() {
        if (session != null && !messageReceived) {
            try {
                session.sendMessage(new TextMessage("ping"));
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
        taskScheduler.scheduleAtFixedRate(this::sendPingMessage, pingInterval);
    }
}
