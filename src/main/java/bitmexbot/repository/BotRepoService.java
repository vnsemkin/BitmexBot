package bitmexbot.repository;

import bitmexbot.entity.BitmexBot;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BotRepoService {
    private final BotRepo botRepo;

    public BotRepoService(BotRepo botRepo) {
        this.botRepo = botRepo;
    }


    public BitmexBot findByBotId(int botId) {
        return botRepo.findByBotId(botId).isPresent() ?
                botRepo.findByBotId(botId).get() : new BitmexBot();
    }

    public BitmexBot findBotByBitmexOrder(String orderId) {
        return botRepo.findBotByBitmexOrder(orderId).isPresent() ?
                botRepo.findBotByBitmexOrder(orderId).get() : new BitmexBot();
    }

    public List<BitmexBot> findBotsWithOrders() {
        return botRepo.findAllBotWithOrders();
    }


    public void updateBot(BitmexBot bitmexBot) {
        botRepo.saveAndFlush(bitmexBot);
    }

    public BitmexBot createBot(BitmexBot bitmexBot) {
        return botRepo.save(bitmexBot);
    }

    public List<BitmexBot> findAll() {
        return botRepo.findAll();
    }

    public List<BitmexBot> findAllBotWithOrders() {
        return botRepo.findAllBotWithOrders();
    }

    @Transactional
    public void removeByBotId(int botId) {
       botRepo.removeByBotId(botId);
    }
}
