package bitmexbot.controller;

import bitmexbot.config.Strategy;
import bitmexbot.dto.BotDto;
import bitmexbot.dto.mapper.BotMapper;
import bitmexbot.model.UserBotParam;
import bitmexbot.entity.BotDataEntity;
import bitmexbot.service.repo.BotRepoService;
import bitmexbot.service.BotFactory;
import bitmexbot.service.UserInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@org.springframework.stereotype.Controller
public class Controller {
    private final UserInfoService userInfoService;
    private final BotFactory botFactory;
    @Value("${bitmex.token.key}")
    private String key;
    @Value("${bitmex.token.secret}")
    private String secret;
    private final BotRepoService botRepoService;
    private final BotMapper botMapper;

    public Controller(UserInfoService userInfoService
            , BotFactory botFactory
            , BotRepoService botRepoService,
                      BotMapper botMapper) {
        this.botFactory = botFactory;
        this.userInfoService = userInfoService;
        this.botRepoService = botRepoService;
        this.botMapper = botMapper;
    }

    @GetMapping("/")
    public String getIndexPage() {
        return "redirect:/bot";
    }

    @GetMapping("/bot")
    public String getBots(Model model) {
        List<BotDto> botList = botMapper.toDtoList(botRepoService.findAll());
        List<String> strategies = new ArrayList<>(Arrays.stream(Strategy.values())
                .map(Strategy::getLabel)
                .toList());
        strategies.replaceAll(s -> s.replaceAll("\\\\[|\\\\]", ""));
        model.addAttribute("strategies", strategies);
        model.addAttribute("botList", botList);
        return "home";
    }

    @PostMapping("/bot")
    public String createBot(@Valid UserBotParam userBotParam
            , Model model) {
        userBotParam.setKey(key);
        userBotParam.setSecret(secret);
        //
        List<BotDto> botList;

        // Set User info and order book last buy and sell prices
        BotDataEntity botDataEntity = userInfoService.getUserInfo(userBotParam);
        // Get bots if they are
        botList = botMapper.toDtoList(botFactory
                .createNewBot(botDataEntity));

        model.addAttribute("botList", botList);
        return "home";
    }

    @DeleteMapping("/bot/{id}")
    public String removeBot(@PathVariable int id, Model model) {
        List<BotDto> botList = botMapper.toDtoList(botFactory.removeBot(id));
        model.addAttribute("botList", botList);
        return "home";
    }
}

