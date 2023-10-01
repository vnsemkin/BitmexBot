package bitmexbot.service;


import bitmexbot.config.BitmexConstants;
import bitmexbot.config.BitmexEndpoints;
import bitmexbot.dto.UserBotParamDTO;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.model.*;
import bitmexbot.output.BitmexFeignClient;
import bitmexbot.util.authorization.APIAuthDataService;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {
    private final BitmexFeignClient bitmexFeignClient;

    public UserInfoService(BitmexFeignClient bitmexFeignClient) {
        this.bitmexFeignClient = bitmexFeignClient;
    }

    public BitmexBotData getUserInfo(UserBotParamDTO userBotParamDTO) {
        String emptyData = "";
        BitmexBotData bitmexBotData = new BitmexBotData();
        // Set data from userBotParam
        bitmexBotData.setKey(userBotParamDTO.getKey());
        bitmexBotData.setSecret(userBotParamDTO.getSecret());
        bitmexBotData.setLevel(userBotParamDTO.getLevel());
        bitmexBotData.setStep(userBotParamDTO.getStep());
        bitmexBotData.setCoefficient(userBotParamDTO.getCoefficient());
        bitmexBotData.setStrategy(userBotParamDTO.getStrategy());
        //
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

        QuoteRequest quoteRequest = new QuoteRequest(BitmexConstants.XBTUSDT_SYMBOL, 1, true);

        //Get the latest Bid and Ask
        List<QuoteResponse> quoteResponse = bitmexFeignClient.getQuote(quoteRequest.getSymbol()
                , quoteRequest.getCount()
                , quoteRequest.isReverse());

        // Set data from quote  response
        bitmexBotData.setLastBuy(quoteResponse.get(0).getLastBid());
        bitmexBotData.setLastSell(quoteResponse.get(0).getLastAsk());
        bitmexBotData.setUserName(user.getUserName());
        bitmexBotData.setUserEmail(user.getEmail());
        bitmexBotData.setUserAccount(userWallet.getAccount());
        bitmexBotData.setUserCurrency(userWallet.getCurrency());
        bitmexBotData.setCoefficient(bitmexBotData.getCoefficient() * 10000);
        return bitmexBotData;
    }
}
