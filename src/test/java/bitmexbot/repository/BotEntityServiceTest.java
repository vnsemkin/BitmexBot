package bitmexbot.repository;

import bitmexbot.entity.BotEntity;
import bitmexbot.entity.BotDataEntity;
import bitmexbot.entity.BotOrderEntity;
import bitmexbot.service.repo.BotRepoService;
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
import java.util.Set;


@SpringBootTest
@Sql("/populate_db.sql")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BotEntityServiceTest {
    @Autowired
    BotRepoService botRepoService;

    @SpyBean
    private BotRepoService botRepoMock;

    @Test
    public void shouldReturnBotDtoByBotId() {
        Mockito.when(botRepoMock.findByBotId(1)).thenReturn(getBitmexBot());
        BotEntity botEntityById = botRepoService.findByBotId(1);
        Assertions.assertNotNull(botEntityById);
        Mockito.verify(botRepoMock, Mockito.times(1)).findByBotId(1);
    }

    private BotEntity getBitmexBot() {
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
}
