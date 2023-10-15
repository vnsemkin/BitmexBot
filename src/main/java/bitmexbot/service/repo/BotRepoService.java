package bitmexbot.service.repo;

import bitmexbot.entity.BotEntity;
import bitmexbot.repository.BotRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BotRepoService {
    private final BotRepo botRepo;

    public BotRepoService(BotRepo botRepo) {
        this.botRepo = botRepo;
    }

    public BotEntity save(BotEntity botEntity) {
        return botRepo.saveAndFlush(botEntity);
    }

    public BotEntity findById(long id) {
        return botRepo.findById(id).isPresent()
                ? botRepo.findById(id).get() : new BotEntity();
    }


    public BotEntity findByBotId(int botId) {
        return botRepo.findByBotId(botId).isPresent() ?
                botRepo.findByBotId(botId).get() : new BotEntity();
    }

    public BotEntity findBotByBitmexOrder(String orderId) {
        return botRepo.findBotByBotOrder(orderId).isPresent() ?
                botRepo.findBotByBotOrder(orderId).get() : new BotEntity();
    }

    public List<BotEntity> findBotsWithOrders() {
        return botRepo.findAllBotWithOrders();
    }


    public void updateBot(BotEntity botEntity) {
        botRepo.saveAndFlush(botEntity);
    }

    public BotEntity createBot(BotEntity botEntity) {
        return botRepo.save(botEntity);
    }

    @Transactional
    public List<BotEntity> findAll() {
        return botRepo.findAll();
    }

    public List<BotEntity> findAllBotWithOrders() {
        return botRepo.findAllBotWithOrders();
    }

    @Transactional
    public void removeByBotId(int botId) {
        botRepo.removeByBotId(botId);
    }
}
