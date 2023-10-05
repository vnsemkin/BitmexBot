package bitmexbot.repository;

import bitmexbot.dto.BotDTO;
import bitmexbot.entity.BitmexBot;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.entity.BitmexOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@SpringBootTest
@Sql("/script.sql")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BotRepoServiceTest {
    @Autowired
    BotRepoService botRepoService;

    @SpyBean
    private BotRepo botRepoMock;

    @Test
    public void should_return_Bot_DTO_By_BotId(){
        Mockito.when(botRepoMock.findByBotId(1)).thenReturn(Optional.of(getBitmexBot()));
        BotDTO byBotId = botRepoService.findByBotId(1);

        Assertions.assertNotNull(byBotId);
        Mockito.verify(botRepoMock, Mockito.times(1)).findByBotId(1);
    }

    private BitmexBot getBitmexBot() {
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
