package bitmexbot.controller;

import bitmexbot.config.BotConstants;
import bitmexbot.dto.BotDto;
import bitmexbot.dto.mapper.BotMapper;
import bitmexbot.model.UserBotParam;
import bitmexbot.entity.BotEntity;
import bitmexbot.entity.BotDataEntity;
import bitmexbot.exception.BotNotFoundException;
import bitmexbot.exception.ValidationErrorException;
import bitmexbot.service.repo.BotRepoService;
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
    private final BotRepoService botRepoService;
    private final BotFactory botFactory;
    private final UserInfoService userInfoService;
    private final BotMapper botMapper;

    @Autowired
    public RestController(BotRepoService botRepoService,
                          BotFactory botFactory,
                          UserInfoService userInfoService,
                          BotMapper botMapper) {
        this.botRepoService = botRepoService;
        this.botFactory = botFactory;
        this.userInfoService = userInfoService;
        this.botMapper = botMapper;
    }

    @GetMapping(produces = BotConstants.APP_JSON)
    public ResponseEntity<List<BotDto>> getBotList() {
        return ResponseEntity.ok()
                .body(botMapper.toDtoList(botRepoService.findBotsWithOrders()));
    }

    @GetMapping(value = "/{id}", produces = BotConstants.APP_JSON)
    public ResponseEntity<BotDto> getBotById(@PathVariable int id) {
        BotEntity botByBotIdEntity = botRepoService.findByBotId(id);
        if (botByBotIdEntity.getBotId() != 0) {
            return ResponseEntity.ok().body(botMapper.toDto(botByBotIdEntity));
        } else {
            throw new BotNotFoundException("Бот с id: " + id + " не найден");
        }
    }

    @PostMapping(produces = BotConstants.APP_JSON)
    public ResponseEntity<List<BotDto>> createBot(@Valid @RequestBody UserBotParam userBotParam
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // Handle validation errors
            throw new ValidationErrorException("Validation errors: " + bindingResult.getAllErrors());
        }
        // Set User info and order book last buy and sell prices
        BotDataEntity botDataEntity = userInfoService.getUserInfo(userBotParam);
        //
        // Get bots if they are
        List<BotEntity> botEntityList = botFactory
                .createNewBot(botDataEntity);
        return ResponseEntity.ok()
                .body(botMapper.toDtoList(botEntityList));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBot(@PathVariable int id) {
        botFactory.removeBot(id);
        BotEntity byBotIdEntity = botRepoService.findByBotId(id);
        if (Objects.isNull(byBotIdEntity.getId())) {
            return ResponseEntity.ok().body(BotConstants.BOT_DELETED + " Бот id: (" + id + ")");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Бот c id: " + id + " не найден");
    }
}
