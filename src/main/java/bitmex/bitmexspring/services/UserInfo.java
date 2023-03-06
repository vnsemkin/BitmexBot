package bitmex.bitmexspring.services;


import bitmex.bitmexspring.config.BitmexEndpoints;
import bitmex.bitmexspring.controllers.authorization.APIAuthDataService;
import bitmex.bitmexspring.controllers.web.BitmexFiegnClient;
import bitmex.bitmexspring.models.bitmex.APIAuthData;
import bitmex.bitmexspring.models.bitmex.ClientData;
import bitmex.bitmexspring.models.user.OrderBookRequest;
import bitmex.bitmexspring.models.user.OrderInfo;
import bitmex.bitmexspring.models.user.User;
import bitmex.bitmexspring.models.user.UserWallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserInfo {
    private final BitmexFiegnClient bitmexFiegnClient;
    private APIAuthData authData;
    private final String emptyData = "";


    @Autowired
    public UserInfo(BitmexFiegnClient bitmexFiegnClient) {
        this.bitmexFiegnClient = bitmexFiegnClient;
    }

    public ClientData getUserInfo(ClientData clientData) {
        authData = new APIAuthDataService()
                .getAPIAutData(clientData, String.valueOf(HttpMethod.GET),
                        BitmexEndpoints.USER,
                        emptyData);
        //Get user from http response
        User user = bitmexFiegnClient.getUser(String.valueOf(authData.getApiExpires()),
                authData.getApiKey(),
                authData.getApiSignature());
        authData = new APIAuthDataService()
                .getAPIAutData(clientData, String.valueOf(HttpMethod.GET), BitmexEndpoints.USER_WALLET,
                        emptyData);
        UserWallet userWallet = bitmexFiegnClient.getUserWallet(String.valueOf(authData.getApiExpires()),
                authData.getApiKey(),
                authData.getApiSignature());

        OrderBookRequest orderBookRequest = new OrderBookRequest("XBTUSD", 1);

        authData = new APIAuthDataService()
                .getAPIAutData(clientData, String.valueOf(HttpMethod.GET), BitmexEndpoints.ORDER_BOOK,
                        "");
        //Get the latest Bid and Ask
        List<OrderInfo> orderInfoList = bitmexFiegnClient.getOrderBook(String.valueOf(orderBookRequest.getSymbol()),
                orderBookRequest.getDepth());

        for (OrderInfo order : orderInfoList) {
            if (Objects.equals(order.getSide(), "Buy")) {
                clientData.setLastBuy(order.getPrice());
            } else {
                clientData.setLastSell(order.getPrice());
            }
        }
        clientData.setUserName(user.getUserName());
        clientData.setUserEmail(user.getEmail());
        clientData.setUserAccount(userWallet.getAccount());
        clientData.setUserCurrency(userWallet.getCurrency());
        return clientData;
    }
}
