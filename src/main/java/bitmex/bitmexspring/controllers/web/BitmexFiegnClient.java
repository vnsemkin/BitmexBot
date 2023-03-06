package bitmex.bitmexspring.controllers.web;

import bitmex.bitmexspring.config.BitmexEndpoints;
import bitmex.bitmexspring.models.user.Order;
import bitmex.bitmexspring.models.user.OrderInfo;
import bitmex.bitmexspring.models.user.User;
import bitmex.bitmexspring.models.user.UserWallet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "bitmex",
        url = "${application.url}",
        configuration = FeignClientConf.class)
public interface BitmexFiegnClient {
    @GetMapping(BitmexEndpoints.USER)
    User getUser(@RequestHeader("api-expires") String expires,
                 @RequestHeader("api-key") String key,
                 @RequestHeader("api-signature") String signature);

    @GetMapping(BitmexEndpoints.USER_WALLET)
    UserWallet getUserWallet(@RequestHeader("api-expires") String expires,
                             @RequestHeader("api-key") String key,
                             @RequestHeader("api-signature") String signature);

    @GetMapping(BitmexEndpoints.ORDER_BOOK)
    List<OrderInfo> getOrderBook(@RequestParam("symbol") String symbol,
                                 @RequestParam("depth") int depth);

    @PostMapping(BitmexEndpoints.ORDER)
    String postOrder(@RequestHeader("api-expires") String expires,
                     @RequestHeader("api-key") String key,
                     @RequestHeader("api-signature") String signature,
                     @RequestBody Order order);
}
