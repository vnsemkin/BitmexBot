package bitmexbot.network;

import bitmexbot.config.BotConstants;
import bitmexbot.config.BotEndpoints;
import bitmexbot.entity.BotOrderEntity;
import bitmexbot.model.QuoteResponse;
import bitmexbot.model.User;
import bitmexbot.model.UserWallet;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@org.springframework.cloud.openfeign.FeignClient(name = "bitmex",
        url = "${application.url}",
        configuration = FeignClientConf.class)
public interface FeignClient {
    @GetMapping(BotEndpoints.USER)
    User getUser(@RequestHeader(BotConstants.API_EXPIRES) String expires,
                 @RequestHeader(BotConstants.API_KEY) String key,
                 @RequestHeader(BotConstants.API_SIGNATURE) String signature);

    @GetMapping(BotEndpoints.USER_WALLET)
    UserWallet getUserWallet(@RequestHeader(BotConstants.API_EXPIRES) String expires,
                             @RequestHeader(BotConstants.API_KEY) String key,
                             @RequestHeader(BotConstants.API_SIGNATURE) String signature);

    @GetMapping(BotEndpoints.QUOTE)
    List<QuoteResponse> getQuote(@RequestParam String symbol,
                                 @RequestParam Double count,
                                 @RequestParam Boolean reverse);

    @PostMapping(BotEndpoints.ORDER)
    BotOrderEntity postOrder(@RequestHeader(BotConstants.API_EXPIRES) String expires,
                             @RequestHeader(BotConstants.API_KEY) String key,
                             @RequestHeader(BotConstants.API_SIGNATURE) String signature,
                             @RequestBody BotOrderEntity botOrderEntity);

    @DeleteMapping(BotEndpoints.ORDER)
    List<BotOrderEntity> deleteOrder(@RequestHeader(BotConstants.API_EXPIRES) String expires,
                                     @RequestHeader(BotConstants.API_KEY) String key,
                                     @RequestHeader(BotConstants.API_SIGNATURE) String signature,
                                     @RequestBody Map<String, Set<String>> map);
}
