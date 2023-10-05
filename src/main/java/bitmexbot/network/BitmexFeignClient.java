package bitmexbot.network;

import bitmexbot.config.BitmexConstants;
import bitmexbot.config.BitmexEndpoints;
import bitmexbot.entity.BitmexOrder;
import bitmexbot.model.QuoteResponse;
import bitmexbot.model.User;
import bitmexbot.model.UserWallet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(name = "bitmex",
        url = "${application.url}",
        configuration = FeignClientConf.class)
public interface BitmexFeignClient {
    @GetMapping(BitmexEndpoints.USER)
    User getUser(@RequestHeader(BitmexConstants.API_EXPIRES) String expires,
                 @RequestHeader(BitmexConstants.API_KEY) String key,
                 @RequestHeader(BitmexConstants.API_SIGNATURE) String signature);

    @GetMapping(BitmexEndpoints.USER_WALLET)
    UserWallet getUserWallet(@RequestHeader(BitmexConstants.API_EXPIRES) String expires,
                             @RequestHeader(BitmexConstants.API_KEY) String key,
                             @RequestHeader(BitmexConstants.API_SIGNATURE) String signature);

    @GetMapping(BitmexEndpoints.QUOTE)
    List<QuoteResponse> getQuote(@RequestParam String symbol,
                                 @RequestParam Double count,
                                 @RequestParam Boolean reverse);

    @PostMapping(BitmexEndpoints.ORDER)
    BitmexOrder postOrder(@RequestHeader(BitmexConstants.API_EXPIRES) String expires,
                          @RequestHeader(BitmexConstants.API_KEY) String key,
                          @RequestHeader(BitmexConstants.API_SIGNATURE) String signature,
                          @RequestBody BitmexOrder bitmexOrder);

    @DeleteMapping(BitmexEndpoints.ORDER)
    List<BitmexOrder> deleteOrder(@RequestHeader(BitmexConstants.API_EXPIRES) String expires,
                                  @RequestHeader(BitmexConstants.API_KEY) String key,
                                  @RequestHeader(BitmexConstants.API_SIGNATURE) String signature,
                                  @RequestBody Map<String, Set<String>> map);
}
