package bitmex.bitmexspring.controllers.web;

import bitmex.bitmexspring.config.BitmexConstants;
import bitmex.bitmexspring.config.BitmexEndpoints;
import bitmex.bitmexspring.models.user.Order;
import bitmex.bitmexspring.models.user.OrderInfo;
import bitmex.bitmexspring.models.user.User;
import bitmex.bitmexspring.models.user.UserWallet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(name = "bitmex",
        url = "${application.url}",
        configuration = FeignClientConf.class)
public interface BitmexFiegnClient {
    @GetMapping(BitmexEndpoints.USER)
    User getUser(@RequestHeader(BitmexConstants.API_EXPIRES) String expires,
                 @RequestHeader(BitmexConstants.API_KEY) String key,
                 @RequestHeader(BitmexConstants.API_SIGNATURE) String signature);

    @GetMapping(BitmexEndpoints.USER_WALLET)
    UserWallet getUserWallet(@RequestHeader(BitmexConstants.API_EXPIRES) String expires,
                             @RequestHeader(BitmexConstants.API_KEY) String key,
                             @RequestHeader(BitmexConstants.API_SIGNATURE) String signature);

    @GetMapping(BitmexEndpoints.ORDER_BOOK)
    List<OrderInfo> getOrderBook(@RequestParam("symbol") String symbol,
                                 @RequestParam("depth") int depth);

    @PostMapping(BitmexEndpoints.ORDER)
    Order postOrder(@RequestHeader(BitmexConstants.API_EXPIRES) String expires,
                     @RequestHeader(BitmexConstants.API_KEY) String key,
                     @RequestHeader(BitmexConstants.API_SIGNATURE) String signature,
                     @RequestBody Order order);

    @DeleteMapping (BitmexEndpoints.ORDER)
    List<Order> deleteOrder(@RequestHeader(BitmexConstants.API_EXPIRES) String expires,
                      @RequestHeader(BitmexConstants.API_KEY) String key,
                      @RequestHeader(BitmexConstants.API_SIGNATURE) String signature,
                      @RequestBody Map<String, Set<String>> map);
}
