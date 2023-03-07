package bitmex.bitmexspring.services;

import bitmex.bitmexspring.controllers.json.JsonController;
import bitmex.bitmexspring.models.bitmex.ClientData;
import bitmex.bitmexspring.models.bitmex.WSOrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;

@Service
public class WSHandler extends TextWebSocketHandler {
    private final StringBuilder message = new StringBuilder();
    private final StringBuilder cache = new StringBuilder();
    private final StringBuilder pong = new StringBuilder("pong");
    private final JsonController json;
    private WSOrderStatus wsOrderStatus;
    private final OrderPost orderPost;
    private ClientData clientData;

    @Autowired
    public WSHandler(JsonController json, OrderPost orderPost) {
        this.json = json;
        this.orderPost = orderPost;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
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
                        orderPost.sell();
                    } else if (wsOrderStatus.getOrder().get(0).getSide().equals("Sell")
                            && wsOrderStatus.getOrder().get(0).getOrdStatus().equals("Filled")) {
                        clientData.setFilledPrice(wsOrderStatus.getOrder().get(0).getPrice());
                        orderPost.setClientData(clientData);
                        orderPost.buy();
                    }
                }
            }
            message.setLength(0);
        }
        message.setLength(0);
    }
}
