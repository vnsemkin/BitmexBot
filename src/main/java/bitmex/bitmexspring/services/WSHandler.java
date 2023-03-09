package bitmex.bitmexspring.services;

import bitmex.bitmexspring.controllers.endpoints.IndexController;
import bitmex.bitmexspring.controllers.json.JsonController;
import bitmex.bitmexspring.models.bitmex.ClientData;
import bitmex.bitmexspring.models.bitmex.WSOrderStatus;
import bitmex.bitmexspring.models.user.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;

@Service
public class WSHandler extends TextWebSocketHandler {
    private final ApplicationContext context;
    private final StringBuilder message = new StringBuilder();
    private final StringBuilder cache = new StringBuilder();
    private final StringBuilder pong = new StringBuilder("pong");
    private final JsonController json;
    private WSOrderStatus wsOrderStatus;
    private final OrderPost orderPost;
    private ClientData clientData;

    @Autowired
    public WSHandler(ApplicationContext context, JsonController json, OrderPost orderPost) {
        this.context = context;
        this.json = json;
        this.orderPost = orderPost;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        IndexController indexController= context.getBean(IndexController.class);
        message.append(textMessage.getPayload());
        if (!message.toString().equals(pong.toString())) {
            if (!cache.toString().equals(message.toString())) {
                cache.setLength(0);
                cache.append(message);
                wsOrderStatus = (WSOrderStatus) json.readToObject(String.valueOf(cache), WSOrderStatus.class);
                if (Objects.nonNull(wsOrderStatus.getOrder()) && !wsOrderStatus.getAction().equals("partial")) {
                    wsOrderStatus.getOrder().forEach(ord -> System.out.println(wsOrderStatus.getAction()
                            + ": Side " + ord.getSide()
                            + ", Status " + ord.getOrdStatus()
                            + ", id: " + ord.getId()
                            + ", price" + ord.getPrice()
                    ));
                    if (wsOrderStatus.getOrder().get(0).getSide().equals("Buy")
                            && wsOrderStatus.getOrder().get(0).getOrdStatus().equals("Filled")) {
                        clientData.setFilledPrice(wsOrderStatus.getOrder().get(0).getPrice());
                        orderPost.setClientData(clientData);
                        Order order = orderPost.sell();
                        for (BitmexBot bot : indexController.getBotList()) {
                            bot.getOrderList().removeIf(ord -> Objects.equals(ord.getId(), wsOrderStatus.getOrder().get(0).getId()));
                            bot.getOrderList().add(order);
                        }
                    } else if (wsOrderStatus.getOrder().get(0).getSide().equals("Sell")
                            && wsOrderStatus.getOrder().get(0).getOrdStatus().equals("Filled")) {
                        clientData.setFilledPrice(wsOrderStatus.getOrder().get(0).getPrice());
                        orderPost.setClientData(clientData);
                        Order order = orderPost.buy();
                        for (BitmexBot bot : indexController.getBotList()) {
                            bot.getOrderList().removeIf(ord -> Objects.equals(ord.getId(), wsOrderStatus.getOrder().get(0).getId()));
                            bot.getOrderList().add(order);
                        }
                    }else if(wsOrderStatus.getOrder().get(0).getOrdStatus().equals("Canceled")){
                        for (BitmexBot bot : indexController.getBotList()) {
                            bot.getOrderList().removeIf(ord -> Objects.equals(ord.getId(), wsOrderStatus.getOrder().get(0).getId()));
                        }
                    }
                }
            }
            message.setLength(0);
        }
        message.setLength(0);
    }
}
