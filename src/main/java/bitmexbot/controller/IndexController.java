package bitmexbot.controller;

import bitmexbot.entity.BitmexBot;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.repository.BotRepo;
import bitmexbot.service.BotFactory;
import bitmexbot.service.UserInfoService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Controller
public class IndexController {
    private final UserInfoService userInfoService;
    private final BotFactory botFactory;
    @Getter
    private List<BitmexBot> botList;
    @Value("${bitmex.token.key}")
    private String key;
    @Value("${bitmex.token.secret}")
    private String secret;
    private final BotRepo botRepo;

    public IndexController(UserInfoService userInfoService
            , BotFactory botFactory
            , BotRepo botRepo
    ) {
        this.botFactory = botFactory;
        this.userInfoService = userInfoService;
        this.botRepo = botRepo;
    }


    @GetMapping("/")
    public String getHandler(Model model) {
        botList = botRepo.findAll();
        model.addAttribute("botList", botList);
        return "home";
    }

    @PostMapping("/")
    public String postHandler(@RequestParam(value = "id", required = false) String id,
                              BitmexBotData bitmexBotData,
                              Model model) {
        if (Objects.nonNull(id)) {
            botList = botFactory.deleteBot(Integer.parseInt(id));
            model.addAttribute("botList", botList);
            return "home";
        }

        //For test purpose only
        bitmexBotData.setKey(key);
        bitmexBotData.setSecret(secret);
        //
        // Set User info and order book last buy and sell prices
        bitmexBotData = userInfoService.getUserInfo(bitmexBotData);
        //
        // Get bots if they are
        botList = botFactory.createNewBotAndGetExisting(bitmexBotData);
        model.addAttribute("botList", botList);
        return "home";
    }
}

