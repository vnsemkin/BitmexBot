package bitmexbot.service;


import bitmexbot.config.BotConstants;
import bitmexbot.config.BotEndpoints;
import bitmexbot.model.UserBotParam;
import bitmexbot.entity.BotDataEntity;
import bitmexbot.model.*;
import bitmexbot.network.FeignClient;
import bitmexbot.util.authorization.APIAuthDataService;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {
    private final FeignClient feignClient;

    public UserInfoService(FeignClient feignClient) {
        this.feignClient = feignClient;
    }

    public BotDataEntity getUserInfo(UserBotParam userBotParam) {
        String emptyData = "";
        BotDataEntity botDataEntity = new BotDataEntity();
        // Set data from userBotParam
        botDataEntity.setKey(userBotParam.getKey());
        botDataEntity.setSecret(userBotParam.getSecret());
        botDataEntity.setLevel(userBotParam.getLevel());
        botDataEntity.setStep(userBotParam.getStep());
        botDataEntity.setCoefficient(userBotParam.getCoefficient());
        botDataEntity.setStrategy(userBotParam.getStrategy());
        //
        APIAuthData authData = new APIAuthDataService()
                .getAPIAutData(botDataEntity, String.valueOf(HttpMethod.GET),
                        BotEndpoints.USER,
                        emptyData);
        //Get user from http response
        User user = feignClient.getUser(String.valueOf(authData.getApiExpires()),
                authData.getApiKey(),
                authData.getApiSignature());
        authData = new APIAuthDataService()
                .getAPIAutData(botDataEntity, String.valueOf(HttpMethod.GET), BotEndpoints.USER_WALLET,
                        emptyData);
        UserWallet userWallet = feignClient.getUserWallet(String.valueOf(authData.getApiExpires()),
                authData.getApiKey(),
                authData.getApiSignature());

        QuoteRequest quoteRequest = new QuoteRequest(BotConstants.XBTUSDT_SYMBOL, 1, true);

        //Get the latest Bid and Ask
        List<QuoteResponse> quoteResponse = feignClient.getQuote(quoteRequest.getSymbol()
                , quoteRequest.getCount()
                , quoteRequest.isReverse());

        // Set data from quote  response
        botDataEntity.setLastBuy(quoteResponse.get(0).getLastBid());
        botDataEntity.setLastSell(quoteResponse.get(0).getLastAsk());
        botDataEntity.setUserName(user.getUserName());
        botDataEntity.setUserEmail(user.getEmail());
        botDataEntity.setUserAccount(userWallet.getAccount());
        botDataEntity.setUserCurrency(userWallet.getCurrency());
        botDataEntity.setCoefficient(botDataEntity.getCoefficient() * 10000);
        return botDataEntity;
    }
}
