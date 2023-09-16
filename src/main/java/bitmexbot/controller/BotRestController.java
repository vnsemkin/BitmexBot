package bitmexbot.controller;


import bitmexbot.config.BitmexConstants;
import bitmexbot.dto.BotDTO;
import bitmexbot.dto.BotDTOList;
import bitmexbot.entity.BitmexBot;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.repository.BotRepo;
import bitmexbot.service.BotFactory;
import bitmexbot.service.UserInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class BotRestController {
    private final BotRepo botRepo;
    private final BotFactory botFactory;
    private final UserInfoService userInfoService;

    public BotRestController(BotRepo botRepo, BotFactory botFactory, UserInfoService userInfoService) {
        this.botRepo = botRepo;
        this.botFactory = botFactory;
        this.userInfoService = userInfoService;
    }

    @GetMapping(value = "/rest/bot", produces = BitmexConstants.APP_JSON)
    public ResponseEntity<List<BotDTO>> getBotList() {
        List<BitmexBot> all = botRepo.findAll();
        return ResponseEntity.ok().body(all.stream()
                .map(b -> new BotDTO(b.getBotId()
                        , b.getBitmexBotData()
                        , b.getBitmexOrders()))
                .toList());
    }

    @GetMapping(value = "/rest/bot/{id}", produces = BitmexConstants.APP_JSON)
    public ResponseEntity<Object> getBotById(@PathVariable int id) {
        Optional<BitmexBot> botByBotId = botRepo.findByBotId(id);
        if (botByBotId.isPresent()) {
            BitmexBot bitmexBot = botByBotId.get();
            return ResponseEntity.ok().body(new BotDTO(bitmexBot.getBotId()
                    , bitmexBot.getBitmexBotData()
                    , bitmexBot.getBitmexOrders()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Бот с id " + id + " не найден");
    }

    @PostMapping(value = "/rest/bot", produces = BitmexConstants.APP_JSON)
    public ResponseEntity<List<BotDTO>> postHandler(BitmexBotData bitmexBotData) {

        List<BotDTO> botList;
        // Set User info and order book last buy and sell prices
        bitmexBotData = userInfoService.getUserInfo(bitmexBotData);
        //
        // Get bots if they are
        botList = BotDTOList.of(botFactory
                .createNewBot(bitmexBotData));
        return ResponseEntity.ok()
                .body(botList);
    }

    @DeleteMapping("/rest/bot/{id}")
    public ResponseEntity<String> deleteBot(@PathVariable int id) {
        botFactory.deleteBot(id);
        if (Objects.isNull(botRepo.findByBotId(id))) {
            return ResponseEntity.ok().body("Бот с ордерами успешно удален");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Бот не найден");
    }
}
