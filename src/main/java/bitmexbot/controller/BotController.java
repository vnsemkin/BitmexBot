package bitmexbot.controller;

import bitmexbot.config.Strategy;
import bitmexbot.dto.BotDTO;
import bitmexbot.dto.BotDTOList;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.repository.BotRepo;
import bitmexbot.service.BotFactory;
import bitmexbot.service.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class BotController {
    private final UserInfoService userInfoService;
    private final BotFactory botFactory;
    @Value("${bitmex.token.key}")
    private String key;
    @Value("${bitmex.token.secret}")
    private String secret;
    private final BotRepo botRepo;

    public BotController(UserInfoService userInfoService
            , BotFactory botFactory
            , BotRepo botRepo
    ) {
        this.botFactory = botFactory;
        this.userInfoService = userInfoService;
        this.botRepo = botRepo;
    }

    @GetMapping("/bot")
    public String getBots(Model model) {
        List<BotDTO> botList;
        botList = BotDTOList.of(botRepo.findBotWithOrders());
        List<String> strategies = new ArrayList<>(Arrays.stream(Strategy.values())
                .map(Strategy::getLabel)
                .toList());
        strategies.replaceAll(s->s.replaceAll("\\\\[|\\\\]",""));
        model.addAttribute("strategies", strategies);
        model.addAttribute("botList", botList);
        return "home";
    }

    @PostMapping("/bot")
    public String postHandler(BitmexBotData bitmexBotData, Model model) {
        //For test purpose only
        bitmexBotData.setKey(key);
        bitmexBotData.setSecret(secret);
        //
        List<BotDTO> botList;
        // Set User info and order book last buy and sell prices
        bitmexBotData = userInfoService.getUserInfo(bitmexBotData);
        //

        // Get bots if they are
        botList = BotDTOList.of(botFactory
                .createNewBot(bitmexBotData));
        model.addAttribute("botList", botList);
        return "home";
    }
    @DeleteMapping("/bot/{id}")
    public String deleteBot( @PathVariable int id, Model model){
        botRepo.deleteByBotId(id);
        model.addAttribute("botList", BotDTOList.of(botRepo.findBotWithOrders()));
        return "home";
    }
}

