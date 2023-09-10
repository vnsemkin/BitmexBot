package bitmexbot.service;


import bitmexbot.config.BitmexConstants;
import bitmexbot.config.BitmexEndpoints;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.model.bitmex.APIAuthData;
import bitmexbot.model.user.OrderBookRequest;
import bitmexbot.model.user.OrderInfo;
import bitmexbot.model.user.User;
import bitmexbot.model.user.UserWallet;
import bitmexbot.util.authorization.APIAuthDataService;
import bitmexbot.util.web.BitmexFeignClient;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {
    private final BitmexFeignClient bitmexFeignClient;

    public UserInfoService(BitmexFeignClient bitmexFeignClient) {
        this.bitmexFeignClient = bitmexFeignClient;
    }

    public BitmexBotData getUserInfo(BitmexBotData bitmexBotData) {
        String emptyData = "";
        APIAuthData authData = new APIAuthDataService()
                .getAPIAutData(bitmexBotData, String.valueOf(HttpMethod.GET),
                        BitmexEndpoints.USER,
                        emptyData);
        //Get user from http response
        User user = bitmexFeignClient.getUser(String.valueOf(authData.getApiExpires()),
                authData.getApiKey(),
                authData.getApiSignature());
        authData = new APIAuthDataService()
                .getAPIAutData(bitmexBotData, String.valueOf(HttpMethod.GET), BitmexEndpoints.USER_WALLET,
                        emptyData);
        UserWallet userWallet = bitmexFeignClient.getUserWallet(String.valueOf(authData.getApiExpires()),
                authData.getApiKey(),
                authData.getApiSignature());

        OrderBookRequest orderBookRequest = new OrderBookRequest(BitmexConstants.XBTUSDT_SYMBOL, 1);

        //Get the latest Bid and Ask
        List<OrderInfo> orderInfoList = bitmexFeignClient.getOrderBook(String.valueOf(orderBookRequest.getSymbol()),
                orderBookRequest.getDepth());

        for (OrderInfo order : orderInfoList) {
            if (order.getSide().equalsIgnoreCase(BitmexConstants.ORDER_BUY)) {
                bitmexBotData.setLastBuy(order.getPrice());
            } else {
                bitmexBotData.setLastSell(order.getPrice());
            }
        }
        bitmexBotData.setUserName(user.getUserName());
        bitmexBotData.setUserEmail(user.getEmail());
        bitmexBotData.setUserAccount(userWallet.getAccount());
        bitmexBotData.setUserCurrency(userWallet.getCurrency());
        bitmexBotData.setCoefficient(bitmexBotData.getCoefficient() * 10000);
        return bitmexBotData;
    }
}
