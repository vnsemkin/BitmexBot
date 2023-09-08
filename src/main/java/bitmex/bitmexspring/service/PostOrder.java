package bitmex.bitmexspring.service;

import bitmex.bitmexspring.config.BitmexConstants;
import bitmex.bitmexspring.config.BitmexEndpoints;
import bitmex.bitmexspring.entity.BitmexBot;
import bitmex.bitmexspring.entity.BitmexBotData;
import bitmex.bitmexspring.entity.BitmexOrder;
import bitmex.bitmexspring.model.bitmex.APIAuthData;
import bitmex.bitmexspring.repository.jpa.BotRepo;
import bitmex.bitmexspring.util.authorization.APIAuthDataService;
import bitmex.bitmexspring.util.json.JsonParser;
import bitmex.bitmexspring.util.web.BitmexFeignClient;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostOrder {
    private APIAuthData authData;
    private final JsonParser json;
    private final BitmexFeignClient bitmexFeignClient;
    private final BotRepo botRepo;

    public PostOrder(BitmexFeignClient bitmexFeignClient
            , JsonParser json
            , BotRepo botRepo) {
        this.bitmexFeignClient = bitmexFeignClient;
        this.json = json;
        this.botRepo = botRepo;
    }


    public void initialBuy(BitmexBot bitmexBot) {
        Set<BitmexOrder> bitmexOrderList = bitmexBot.getBitmexOrders();
        BitmexBotData bitmexBotData = bitmexBot.getBitmexBotData();
        for (int i = 1; i < bitmexBotData.getLevel() + 1; i++) {
            BitmexOrder bitmexOrder = new BitmexOrder();
            bitmexOrder.setSymbol(BitmexConstants.XBT_USDT_SYMBOL);
            bitmexOrder.setSide(BitmexConstants.ORDER_BUY);
            bitmexOrder.setPrice(bitmexBotData.getLastBuy() - bitmexBotData.getStep() * i);
            bitmexOrder.setOrderQty(bitmexBotData.getCoefficient());
            authData = new APIAuthDataService()
                    .getAPIAutData(bitmexBotData, String.valueOf(HttpMethod.POST), BitmexEndpoints.ORDER,
                            json.writeToString(bitmexOrder));
            BitmexOrder response = bitmexFeignClient.postOrder(String.valueOf(authData.getApiExpires())
                    , authData.getApiKey(),
                    authData.getApiSignature(), bitmexOrder);
            bitmexOrderList.add(response);
        }
        botRepo.updateBot(bitmexBot);
    }

    public BitmexOrder buy(BitmexBot bitmexBot
            , BitmexOrder bitmexOrder) {
        BitmexBotData bitmexBotData = bitmexBot.getBitmexBotData();

        BitmexOrder newOrder = new BitmexOrder();
        newOrder.setSymbol(BitmexConstants.XBT_USDT_SYMBOL);
        newOrder.setSide(BitmexConstants.ORDER_BUY);
        newOrder.setPrice(bitmexOrder.getFilledPrice() - bitmexBotData.getStep());
        newOrder.setOrderQty(bitmexBotData.getCoefficient());
        authData = new APIAuthDataService()
                .getAPIAutData(bitmexBotData, String.valueOf(HttpMethod.POST), BitmexEndpoints.ORDER,
                        json.writeToString(bitmexOrder));
        return bitmexFeignClient.postOrder(String.valueOf(authData.getApiExpires())
                , authData.getApiKey(),
                authData.getApiSignature(), bitmexOrder);
    }

    public void delete(BitmexBot bitmexBot) {
        BitmexBotData bitmexBotData = bitmexBot.getBitmexBotData();
        String name = BitmexConstants.ORDER_ID;
        Set<String> idSet = bitmexBot.getBitmexOrders()
                .stream()
                .map(BitmexOrder::getOrderId)
                .collect(Collectors
                        .toSet());
        Map<String, Set<String>> map = new HashMap<>();
        map.put(name, idSet);
        authData = new APIAuthDataService()
                .getAPIAutData(bitmexBotData, String.valueOf(HttpMethod.DELETE), BitmexEndpoints.ORDER,
                        json.writeToString(map));
        bitmexFeignClient.deleteOrder(String.valueOf(authData.getApiExpires())
                , authData.getApiKey(),
                authData.getApiSignature(), map);
    }

    public BitmexOrder sell(BitmexBot bitmexBot
            , BitmexOrder bitmexOrder) {
        BitmexBotData bitmexBotData = bitmexBot.getBitmexBotData();
        BitmexOrder order = new BitmexOrder();
        order.setSymbol(BitmexConstants.XBT_USDT_SYMBOL);
        order.setSide(BitmexConstants.ORDER_SELL);
        order.setPrice(bitmexOrder.getFilledPrice() + bitmexBotData.getStep());
        order.setOrderQty(bitmexBotData.getCoefficient());
        authData = new APIAuthDataService()
                .getAPIAutData(bitmexBotData, String.valueOf(HttpMethod.POST), BitmexEndpoints.ORDER,
                        json.writeToString(bitmexOrder));
        return bitmexFeignClient.postOrder(String.valueOf(authData.getApiExpires())
                , authData.getApiKey(),
                authData.getApiSignature(), bitmexOrder);
    }
}
