package bitmexbot.controller;


import bitmexbot.config.BitmexConstants;
import bitmexbot.dto.BotDTO;
import bitmexbot.dto.BotDTOList;
import bitmexbot.dto.UserBotParam;
import bitmexbot.entity.BitmexBot;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.exception.BotNotFoundException;
import bitmexbot.repository.BotRepo;
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
    private final BotRepo botRepo;
    private final BotFactory botFactory;
    private final UserInfoService userInfoService;

    public BotRestController(BotRepo botRepo
            , BotFactory botFactory
            , UserInfoService userInfoService) {
        this.botRepo = botRepo;
        this.botFactory = botFactory;
        this.userInfoService = userInfoService;
    }

    @GetMapping(value = "/bot", produces = BitmexConstants.APP_JSON)
    public ResponseEntity<List<BotDTO>> getBotList() {
        List<BitmexBot> all = botRepo.findAll();
        return ResponseEntity.ok().body(all.stream()
                .map(b -> new BotDTO(b.getBotId()
                        , b.getBitmexBotData()
                        , b.getBitmexOrders()))
                .toList());
    }

    @GetMapping(value = "/bot/{id}", produces = BitmexConstants.APP_JSON)
    public ResponseEntity<BotDTO> getBotById(@PathVariable int id) {
        return botRepo.findByBotId(id)
                .map(bitmexBot -> ResponseEntity.ok().body(new BotDTO(
                        bitmexBot.getBotId(),
                        bitmexBot.getBitmexBotData(),
                        bitmexBot.getBitmexOrders()
                )))
                .orElseThrow(() -> new BotNotFoundException("Бот с id " + id + " не найден"));
    }

    @PostMapping(value = "/bot", produces = BitmexConstants.APP_JSON)
    public ResponseEntity<Object> createBot(@Valid @RequestBody UserBotParam userBotParam
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors
            return ResponseEntity.badRequest().body("Validation errors: " + bindingResult.getAllErrors());
        }
        List<BotDTO> botList;
        // Set User info and order book last buy and sell prices
        BitmexBotData bitmexBotData = userInfoService.getUserInfo(userBotParam);
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
        if (Objects.isNull(botRepo.findByBotId(id))) {
            return ResponseEntity.ok().body("Бот с ордерами успешно удален");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Бот не найден");
    }
}
