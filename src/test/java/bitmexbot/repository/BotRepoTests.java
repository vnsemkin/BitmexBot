package bitmexbot.repository;

import bitmexbot.entity.BitmexBot;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.entity.BitmexOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class BotRepoTests {
    @Autowired
    BotRepo botRepo;
    @Autowired
    OrderRepo orderRepo;

    @Test
    @Transactional
    public void shouldDeleteBot(){
        //GIVEN
        List<BitmexBot> expectedBotList = botRepo.findAll();
        BitmexBot bitmexBot = getBitmexBot();
        //WHEN
        BitmexBot botFromDB = botRepo.createBot(bitmexBot);
        botRepo.deleteByBotId(botFromDB.getBotId());
        List<BitmexBot> actualBotList = botRepo.findAll();
        //THEN
        Assertions.assertEquals(botFromDB, bitmexBot);
        Assertions.assertEquals(expectedBotList, actualBotList);
    }

    private static BitmexBot getBitmexBot() {
        BitmexBot bitmexBot = new BitmexBot();
        BitmexOrder order1 = new BitmexOrder();
        BitmexOrder order2 = new BitmexOrder();
        BitmexBotData bitmexBotData = new BitmexBotData();
        order1.setPrice(10000);
        order2.setPrice(20000);

        Set<BitmexOrder> bitmexOrders = new HashSet<>();
        bitmexOrders.add(order1);
        bitmexOrders.add(order2);
        bitmexBot.setBitmexBotData(bitmexBotData);
        bitmexBot.setBitmexOrders(bitmexOrders);
        bitmexBot.setBotId(Integer.MAX_VALUE);
        return bitmexBot;
    }
}
