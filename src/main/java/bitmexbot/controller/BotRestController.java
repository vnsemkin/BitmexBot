package bitmexbot.controller;


import bitmexbot.dto.BotDTO;
import bitmexbot.entity.BitmexBot;
import bitmexbot.repository.BotRepo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
public class BotRestController {
    private final BotRepo botRepo;

    public BotRestController(BotRepo botRepo) {
        this.botRepo = botRepo;
    }

    @GetMapping(value = "/rest/bot", produces = "application/json")
    public List<BotDTO> getBotList() {
        List<BitmexBot> all = botRepo.findAll();
        return all.stream()
                .map(b -> new BotDTO(b.getBotId()
                        , b.getBitmexBotData()
                        , b.getBitmexOrders()))
                .toList();
    }

    @GetMapping(value = "rest/bot/{id}", produces = "application/json")
    public BotDTO getBotById(@PathVariable int id) {
        BitmexBot botByBotId = botRepo.findByBotId(id);
        return new BotDTO(botByBotId.getBotId()
                , botByBotId.getBitmexBotData()
                ,botByBotId.getBitmexOrders());
    }
}
