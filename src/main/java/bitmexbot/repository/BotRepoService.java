package bitmexbot.repository;

import bitmexbot.dto.BotDTO;
import bitmexbot.entity.BitmexBot;
import bitmexbot.exception.BotNotCreated;
import bitmexbot.exception.BotNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotRepoService {
    private final BotRepo botRepo;

    public BotRepoService(BotRepo botRepo) {
        this.botRepo = botRepo;
    }


    public BotDTO findByBotId(int botId) {
       return botRepo.findByBotId(botId)
                .map(b -> new BotDTO().of(b)).orElse(null);
    }


    public void deleteByBotId(int id) {
        botRepo.deleteByBotId(id);
    }


    public BotDTO findBotByBitmexOrder(String orderId) {
        return botRepo.findBotByBitmexOrder(orderId).map(b -> new BotDTO().of(b))
                .orElseThrow(() -> new BotNotFoundException("Бот с ордером: " + orderId + "не найден"));
    }


    public List<BotDTO> findBotsWithOrders() {
        return botRepo.findBotsWithOrders()
                .map(botsWithOrders -> botsWithOrders.stream().map(b -> new BotDTO().of(b)).toList())
                .orElseThrow(() -> new BotNotFoundException("Боты не найдены"));
    }


    public void updateBot(BitmexBot bitmexBot) {
        botRepo.updateBot(bitmexBot);

    }

    public BotDTO createBot(BitmexBot bitmexBot) {
        return botRepo.createBot(bitmexBot)
                .map(b -> new BotDTO().of(b)).orElseThrow(() -> new BotNotCreated("Ошибка.Бот не создан в БД"));
    }

    public List<BotDTO> findAll() {
        List<BitmexBot> allBots = botRepo.findAll();
        return allBots.stream().map(b -> new BotDTO().of(b)).toList();
    }

}
