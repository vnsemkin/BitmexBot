package bitmexbot.controller;

import bitmexbot.config.Strategy;
import bitmexbot.dto.BotDTO;
import bitmexbot.dto.BotDTOList;
import bitmexbot.dto.UserBotParamDTO;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.repository.BotRepoService;
import bitmexbot.service.BotFactory;
import bitmexbot.service.UserInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    private final BotRepoService botRepoService;

    public BotController(UserInfoService userInfoService
            , BotFactory botFactory
            , BotRepoService botRepoService
    ) {
        this.botFactory = botFactory;
        this.userInfoService = userInfoService;
        this.botRepoService = botRepoService;
    }

    @GetMapping("/")
    public String getIndexPage() {
        return "redirect:/bot";
    }

    @GetMapping("/bot")
    public String getBots(Model model) {
        List<BotDTO> botList;
        botList = botRepoService.findAll();
        List<String> strategies = new ArrayList<>(Arrays.stream(Strategy.values())
                .map(Strategy::getLabel)
                .toList());
        strategies.replaceAll(s -> s.replaceAll("\\\\[|\\\\]", ""));
        model.addAttribute("strategies", strategies);
        model.addAttribute("botList", botList);
        return "home";
    }

    @PostMapping("/bot")
    public String createBot(@Valid UserBotParamDTO userBotParamDTO
            , Model model) {
        //For test purpose only
        userBotParamDTO.setKey(key);
        userBotParamDTO.setSecret(secret);
        //
        List<BotDTO> botList;
        // Set User info and order book last buy and sell prices
        BitmexBotData bitmexBotData = userInfoService.getUserInfo(userBotParamDTO);
        // Get bots if they are
        botList = BotDTOList.of(botFactory
                .createNewBot(bitmexBotData));
        model.addAttribute("botList", botList);
        return "home";
    }

    @DeleteMapping("/bot/{id}")
    public String deleteBot(@PathVariable int id, Model model) {
        List<BotDTO> botList = BotDTOList.of(botFactory.deleteBot(id));
        model.addAttribute("botList", botList);
        return "home";
    }
}

