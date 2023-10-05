package bitmexbot.repository;

import bitmexbot.entity.BitmexBot;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.entity.BitmexOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
public class BotRepoTest {
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
        BitmexBot botFromDB = botRepo.createBot(bitmexBot).get();
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

    @Test
    public void shouldCascadeDeleteBotByBotIdAndOrders(){
        //GIVEN
        BitmexBot bitmexBot = new BitmexBot();
        BitmexBotData bitmexBotData = new BitmexBotData();
        Set<BitmexOrder> bitmexOrders = new HashSet<>();
        BitmexOrder order = new BitmexOrder();
        order.setPrice(1000);
        order.setBitmexBot(bitmexBot);
        order.setPrice(100);
        bitmexOrders.add(order);
        bitmexBot.setBotId(1);
        bitmexBot.setBitmexBotData(bitmexBotData);
        //WHEN
        BitmexBot actualBot = botRepo.createBot(bitmexBot).get();
        botRepo.deleteByBotId(actualBot.getBotId());
        //THEN
        Assertions.assertEquals(bitmexBot, actualBot);
        Assertions.assertThrows(NoSuchElementException.class
                , () -> botRepo.findById(actualBot.getId()).get());
    }
}
