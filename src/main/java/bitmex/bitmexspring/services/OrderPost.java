package bitmex.bitmexspring.services;

import bitmex.bitmexspring.config.BitmexEndpoints;
import bitmex.bitmexspring.controllers.authorization.APIAuthDataService;
import bitmex.bitmexspring.controllers.json.JsonController;
import bitmex.bitmexspring.controllers.logger.LoggingController;
import bitmex.bitmexspring.controllers.web.BitmexFiegnClient;
import bitmex.bitmexspring.models.bitmex.APIAuthData;
import bitmex.bitmexspring.models.bitmex.ClientData;
import bitmex.bitmexspring.models.user.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderPost {
    private APIAuthData authData;
    private ClientData clientData;
    private final JsonController json;
    private final BitmexFiegnClient bitmexFiegnClient;
    private final LoggingController logger;


    public OrderPost(BitmexFiegnClient bitmexFiegnClient, JsonController json, LoggingController logger) {
        this.bitmexFiegnClient = bitmexFiegnClient;
        this.json = json;
        this.logger = logger;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public void initialBuy(BitmexBot bitmexBot) {
        List<Order> orderList = bitmexBot.getOrderList();
        for (int i = 1; i < clientData.getLevel() + 1; i++) {
            Order order = new Order();
            order.setSymbol("XBTUSD");
            order.setSide("Buy");
            order.setPrice(clientData.getLastBuy() - clientData.getStep() * i);
            order.setOrderQty(clientData.getCoefficient());
            authData = new APIAuthDataService()
                    .getAPIAutData(clientData, String.valueOf(HttpMethod.POST), BitmexEndpoints.ORDER,
                            json.writeToString(order));
            Order response = bitmexFiegnClient.postOrder(String.valueOf(authData.getApiExpires())
                    , authData.getApiKey(),
                    authData.getApiSignature(), order);
            orderList.add(response);
        }
    }

    public Order buy() {
        Order order = new Order();
        order.setSymbol("XBTUSD");
        order.setSide("Buy");
        order.setPrice(clientData.getFilledPrice() - clientData.getStep());
        order.setOrderQty(clientData.getCoefficient());
        authData = new APIAuthDataService()
                .getAPIAutData(clientData, String.valueOf(HttpMethod.POST), BitmexEndpoints.ORDER,
                        json.writeToString(order));
        return bitmexFiegnClient.postOrder(String.valueOf(authData.getApiExpires())
                , authData.getApiKey(),
                authData.getApiSignature(), order);
    }

    public void delete(List<Order> orderList){
        String name = "orderID";
        Set<String> idSet = orderList.stream().map(Order::getId).collect(Collectors.toSet());
        Map<String,Set<String>> map = new HashMap<>();
        map.put(name,idSet);
        authData = new APIAuthDataService()
                .getAPIAutData(clientData, String.valueOf(HttpMethod.DELETE), BitmexEndpoints.ORDER,
                        json.writeToString(map));
        bitmexFiegnClient.deleteOrder(String.valueOf(authData.getApiExpires())
                , authData.getApiKey(),
                authData.getApiSignature(), map);
    }

    public Order sell() {
        Order order = new Order();
        order.setSymbol("XBTUSD");
        order.setSide("Sell");
        order.setPrice(clientData.getFilledPrice() + clientData.getStep());
        order.setOrderQty(clientData.getCoefficient());
        authData = new APIAuthDataService()
                .getAPIAutData(clientData, String.valueOf(HttpMethod.POST), BitmexEndpoints.ORDER,
                        json.writeToString(order));
        return bitmexFiegnClient.postOrder(String.valueOf(authData.getApiExpires())
                , authData.getApiKey(),
                authData.getApiSignature(), order);
    }
}
