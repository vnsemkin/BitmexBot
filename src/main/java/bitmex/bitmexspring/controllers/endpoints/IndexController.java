package bitmex.bitmexspring.controllers.endpoints;

import bitmex.bitmexspring.services.OrderPost;
import bitmex.bitmexspring.services.BitmexBot;
import bitmex.bitmexspring.models.bitmex.ClientData;
import bitmex.bitmexspring.services.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class IndexController {
    private ClientData clientData;
    private final BitmexBot bitmexBot;
    private final OrderPost orderPost;
    private final UserInfo userInfo;

    @Autowired
    public IndexController(UserInfo userInfo,
                           BitmexBot bitmexBot,
                           OrderPost orderPost) {
        this.userInfo = userInfo;
        this.bitmexBot = bitmexBot;
        this.orderPost = orderPost;
    }

    @GetMapping("/")
    public String getHandler() {
        return "home";
    }

    @PostMapping("/")
    public String postHandler(ClientData clientData,//Spring parsing parameters and create Object ClientData
                              Model model) {
        this.clientData = clientData;
        if (clientData.getKey() != null && clientData.getSecret() != null) {
            /*Only for test purpose*/
            clientData.setKey("YeOJnM7jXJKV8pf5dYMalLs0");
            clientData.setSecret("He6LhcpmH9oKNqjYu2RaxqsoutyGf2-0VUVPbIdiGCKOx_j2");
            /* only for test purpose */

            clientData = userInfo.getUserInfo(clientData);

            model.addAllAttributes(Map.of(
                    "key", clientData.getKey(),
                    "secret", clientData.getSecret(),
                    "userName", clientData.getUserName(),
                    "userEmail", clientData.getUserEmail(),
                    "userAccount", clientData.getUserAccount(),
                    "userCurrency", clientData.getUserCurrency(),
                    "lastSellPrice", clientData.getLastSell(),
                    "lastBuyPrice", clientData.getLastBuy()));

            /*
            Starting Web Socket
             */
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            bitmexBot.clientData = orderPost.clientData = this.clientData;
            executorService.execute(bitmexBot);
            return "bitmex";
        } else {
            return "home";
        }
    }
}

