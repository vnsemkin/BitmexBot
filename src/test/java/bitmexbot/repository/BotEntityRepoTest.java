package bitmexbot.repository;

import bitmexbot.entity.BotDataEntity;
import bitmexbot.entity.BotEntity;
import bitmexbot.entity.BotOrderEntity;
import bitmexbot.service.repo.BotRepoService;
import bitmexbot.service.repo.OrderRepoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
@SpringBootTest
public class BotEntityRepoTest {
    @Autowired
    BotRepoService botRepoService;
    @Autowired
    OrderRepoService orderRepoService;

    @Test
    @Sql(value = "/clear_db.sql")
    public void shouldDeleteBot() {
        //GIVEN
        List<BotEntity> expectedBotListEntity = botRepoService.findAll();
        BotEntity botEntity = getBitmexBot();
        //WHEN
        BotEntity botEntityFromDB = botRepoService.save(botEntity);
        botRepoService.removeByBotId(botEntityFromDB.getBotId());
        List<BotEntity> actualBotListEntity = botRepoService.findAll();
        //THEN
        Assertions.assertEquals(botEntityFromDB, botEntity);
        Assertions.assertEquals(expectedBotListEntity, actualBotListEntity);
    }

    private static BotEntity getBitmexBot() {
        BotEntity botEntity = new BotEntity();
        BotOrderEntity order1 = new BotOrderEntity();
        BotOrderEntity order2 = new BotOrderEntity();
        BotDataEntity botDataEntity = new BotDataEntity();
        order1.setPrice(10000);
        order2.setPrice(20000);

        Set<BotOrderEntity> botOrderEntities = new HashSet<>();
        botOrderEntities.add(order1);
        botOrderEntities.add(order2);
        botEntity.setBotDataEntity(botDataEntity);
        botEntity.setBotOrderEntities(botOrderEntities);
        botEntity.setBotId(Integer.MAX_VALUE);
        return botEntity;
    }

    @Test
    @Sql(value = "/clear_db.sql")
    public void shouldCascadeDeleteBotByBotIdAndOrders() {
        //GIVEN
        BotEntity botEntity = new BotEntity();
        BotDataEntity botDataEntity = new BotDataEntity();
        BotOrderEntity order = new BotOrderEntity();

        order.setPrice(1000);
        order.setBotEntity(botEntity);
        order.setPrice(100);
        botEntity.setBotDataEntity(botDataEntity);
        //WHEN
        BotEntity actualBotEntity = botRepoService.save(botEntity);
        botRepoService.removeByBotId(actualBotEntity.getBotId());
        //THEN
        Assertions.assertEquals(botEntity, actualBotEntity);
        Assertions.assertNull(botRepoService.findById(actualBotEntity.getId()).getId());
    }
}
