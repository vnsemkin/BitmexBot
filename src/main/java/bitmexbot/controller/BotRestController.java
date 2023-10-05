package bitmexbot.controller;


import bitmexbot.aspect.Logging;
import bitmexbot.config.BitmexConstants;
import bitmexbot.dto.BotDTO;
import bitmexbot.dto.BotDTOList;
import bitmexbot.dto.UserBotParamDTO;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.exception.BotNotFoundException;
import bitmexbot.exception.ValidationErrorException;
import bitmexbot.repository.BotRepoService;
import bitmexbot.service.BotFactory;
import bitmexbot.service.UserInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/rest")
public class BotRestController {
    private final BotRepoService botRepoService;
    private final BotFactory botFactory;
    private final UserInfoService userInfoService;

    public BotRestController(BotRepoService botRepoService
            , BotFactory botFactory
            , UserInfoService userInfoService) {
        this.botRepoService = botRepoService;
        this.botFactory = botFactory;
        this.userInfoService = userInfoService;
    }

    @Logging(message = "Get request")
    @GetMapping(value = "/bot", produces = BitmexConstants.APP_JSON)
    public ResponseEntity<List<BotDTO>> getBotList() {
        return ResponseEntity.ok()
                .body(botRepoService.findBotsWithOrders());
    }

    @GetMapping(value = "/bot/{id}", produces = BitmexConstants.APP_JSON)
    public ResponseEntity<BotDTO> getBotById(@PathVariable int id) {
        BotDTO byBotId = botRepoService.findByBotId(id);
        if (Objects.nonNull(byBotId)) {
            return ResponseEntity.ok().body(botRepoService.findByBotId(id));
        } else {
            throw new BotNotFoundException("Бот с id: " + id + " не найден");
        }

    }

    @PostMapping(value = "/bot", produces = BitmexConstants.APP_JSON)
    public ResponseEntity<List<BotDTO>> createBot(@Valid @RequestBody UserBotParamDTO userBotParamDTO
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors
            throw new ValidationErrorException("Validation errors: " + bindingResult.getAllErrors());
        }
        List<BotDTO> botList;
        // Set User info and order book last buy and sell prices
        BitmexBotData bitmexBotData = userInfoService.getUserInfo(userBotParamDTO);
        //
        // Get bots if they are
        botList = BotDTOList.of(botFactory
                .createNewBot(bitmexBotData));
        return ResponseEntity.ok()
                .body(botList);
    }
    @DeleteMapping("/bot/{id}")
    public ResponseEntity<String> deleteBot(@PathVariable int id) {
        botFactory.deleteBot(id);
        if (Objects.isNull(botRepoService.findByBotId(id))) {
            return ResponseEntity.ok().body(BitmexConstants.BOT_DELETED);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Бот c id: " + id + " не найден");
    }
}
