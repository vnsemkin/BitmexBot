package bitmexbot.service;

import bitmexbot.aspect.Logging;
import bitmexbot.config.BotConstants;
import bitmexbot.config.BotEndpoints;
import bitmexbot.entity.BotEntity;
import bitmexbot.entity.BotDataEntity;
import bitmexbot.entity.BotOrderEntity;
import bitmexbot.model.APIAuthData;
import bitmexbot.network.FeignClient;
import bitmexbot.service.repo.BotRepoService;
import bitmexbot.util.authorization.APIAuthDataService;
import bitmexbot.util.json.JsonParser;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderHandler {
    private APIAuthData authData;
    private final JsonParser json;
    private final FeignClient feignClient;
    private final BotRepoService botRepoService;

    public OrderHandler(FeignClient feignClient
            , JsonParser json
            , BotRepoService botRepoService) {
        this.feignClient = feignClient;
        this.json = json;
        this.botRepoService = botRepoService;
    }

    @Logging(message = "Initial buy")
    public void initialBuy(BotEntity botEntity) {
        Set<BotOrderEntity> orders = botEntity.getBotOrderEntities();
        BotDataEntity botDataEntity = botEntity.getBotDataEntity();
        for (int i = 1; i < botDataEntity.getLevel() + 1; i++) {
            BotOrderEntity botOrderEntity = new BotOrderEntity();
            botOrderEntity.setSymbol(BotConstants.XBT_USDT_SYMBOL);
            botOrderEntity.setSide(BotConstants.ORDER_BUY);
            botOrderEntity.setPrice(botDataEntity.getLastBuy() - botDataEntity.getStep() * i);
            botOrderEntity.setOrderQty(botDataEntity.getCoefficient());
            authData = new APIAuthDataService()
                    .getAPIAutData(botDataEntity, String.valueOf(HttpMethod.POST), BotEndpoints.ORDER,
                            json.writeToString(botOrderEntity));
            BotOrderEntity response = feignClient.postOrder(String.valueOf(authData.getApiExpires())
                    , authData.getApiKey(),
                    authData.getApiSignature(), botOrderEntity);
            response.setBotEntity(botEntity);
            orders.add(response);
        }
        botRepoService.updateBot(botEntity);
    }

    public BotOrderEntity buy(BotEntity botEntity
            , BotOrderEntity botOrderEntity) {
        BotDataEntity botDataEntity = botEntity.getBotDataEntity();

        BotOrderEntity newOrder = new BotOrderEntity();
        newOrder.setSymbol(BotConstants.XBT_USDT_SYMBOL);
        newOrder.setSide(BotConstants.ORDER_BUY);
        newOrder.setPrice(botOrderEntity.getFilledPrice() - botDataEntity.getStep());
        newOrder.setOrderQty(botDataEntity.getCoefficient());
        authData = new APIAuthDataService()
                .getAPIAutData(botDataEntity, String.valueOf(HttpMethod.POST), BotEndpoints.ORDER,
                        json.writeToString(botOrderEntity));
        return feignClient.postOrder(String.valueOf(authData.getApiExpires())
                , authData.getApiKey(),
                authData.getApiSignature(), botOrderEntity);
    }

    public void delete(BotEntity botEntity) {
        BotDataEntity botDataEntity = botEntity.getBotDataEntity();
        String name = BotConstants.ORDER_ID;
        Set<String> idSet = botEntity.getBotOrderEntities()
                .stream()
                .map(BotOrderEntity::getOrderId)
                .collect(Collectors
                        .toSet());
        Map<String, Set<String>> map = new HashMap<>();
        map.put(name, idSet);
        authData = new APIAuthDataService()
                .getAPIAutData(botDataEntity, String.valueOf(HttpMethod.DELETE), BotEndpoints.ORDER,
                        json.writeToString(map));
        feignClient.deleteOrder(String.valueOf(authData.getApiExpires())
                , authData.getApiKey(),
                authData.getApiSignature(), map);
    }

    public BotOrderEntity sell(BotEntity botEntity
            , BotOrderEntity botOrderEntity) {
        BotDataEntity botDataEntity = botEntity.getBotDataEntity();
        BotOrderEntity order = new BotOrderEntity();
        order.setSymbol(BotConstants.XBT_USDT_SYMBOL);
        order.setSide(BotConstants.ORDER_SELL);
        order.setPrice(botOrderEntity.getFilledPrice() + botDataEntity.getStep());
        order.setOrderQty(botDataEntity.getCoefficient());
        authData = new APIAuthDataService()
                .getAPIAutData(botDataEntity, String.valueOf(HttpMethod.POST), BotEndpoints.ORDER,
                        json.writeToString(botOrderEntity));
        return feignClient.postOrder(String.valueOf(authData.getApiExpires())
                , authData.getApiKey(),
                authData.getApiSignature(), botOrderEntity);
    }
}
