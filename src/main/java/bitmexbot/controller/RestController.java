package bitmexbot.controller;

import bitmexbot.config.BitmexConstants;
import bitmexbot.dto.BotDto;
import bitmexbot.dto.BotDtoList;
import bitmexbot.model.UserBotParam;
import bitmexbot.entity.BitmexBot;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.exception.BotNotFoundException;
import bitmexbot.exception.ValidationErrorException;
import bitmexbot.service.repo.BotService;
import bitmexbot.service.BotFactory;
import bitmexbot.service.UserInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@org.springframework.web.bind.annotation.RestController
@RequestMapping("/rest/bot")
public class RestController {
    private final BotService botService;
    private final BotFactory botFactory;
    private final UserInfoService userInfoService;

    @Autowired
    public RestController(BotService botService,
                          BotFactory botFactory,
                          UserInfoService userInfoService) {
        this.botService = botService;
        this.botFactory = botFactory;
        this.userInfoService = userInfoService;
    }

    @GetMapping(produces = BitmexConstants.APP_JSON)
    public ResponseEntity<List<BotDto>> getBotList() {
        return ResponseEntity.ok()
                .body(BotDtoList.of(botService.findBotsWithOrders()));
    }

    @GetMapping(value = "/{id}", produces = BitmexConstants.APP_JSON)
    public ResponseEntity<BotDto> getBotById(@PathVariable int id) {
        BitmexBot botByBotId = botService.findByBotId(id);
        if (botByBotId.getBotId() != 0) {
            return ResponseEntity.ok().body(BotDto.of(botByBotId));
        } else {
            throw new BotNotFoundException("Бот с id: " + id + " не найден");
        }
    }

    @PostMapping(produces = BitmexConstants.APP_JSON)
    public ResponseEntity<List<BotDto>> createBot(@Valid @RequestBody UserBotParam userBotParam
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors
            throw new ValidationErrorException("Validation errors: " + bindingResult.getAllErrors());
        }
        // Set User info and order book last buy and sell prices
        BitmexBotData bitmexBotData = userInfoService.getUserInfo(userBotParam);
        //
        // Get bots if they are
        List<BitmexBot> botList = botFactory
                .createNewBot(bitmexBotData);
        return ResponseEntity.ok()
                .body(BotDtoList.of(botList));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBot(@PathVariable int id) {
        botFactory.removeBot(id);
        BitmexBot byBotId = botService.findByBotId(id);
        if (Objects.isNull(byBotId.getId())) {
            return ResponseEntity.ok().body(BitmexConstants.BOT_DELETED + "Bot id: " + id);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Бот c id: " + id + " не найден");
    }
}
