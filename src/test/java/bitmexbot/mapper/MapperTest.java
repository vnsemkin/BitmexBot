package bitmexbot.mapper;

import bitmexbot.dto.BotDto;
import bitmexbot.dto.BotOrderDto;
import bitmexbot.dto.mapper.BotMapper;
import bitmexbot.dto.mapper.BotOrderMapper;
import bitmexbot.entity.BotEntity;
import bitmexbot.entity.BotOrderEntity;
import bitmexbot.service.repo.BotRepoService;
import bitmexbot.service.repo.OrderRepoService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class MapperTest {
    @Autowired
    BotRepoService botRepoService;
    @Autowired
    OrderRepoService orderRepoService;
    @Autowired
    BotOrderMapper botOrderMapper;
    @Autowired
    BotMapper botMapper;
    @PersistenceContext
    EntityManager entityManager;

    static ResourceDatabasePopulator populator;

    static DataSource dataSource;

    static boolean isInitialized;

    @Autowired
    public MapperTest(DataSource dataSourceBean) {
        dataSource = dataSourceBean;
        populator = new ResourceDatabasePopulator();
    }


    @BeforeEach
    void initDatabase() {
        if(!isInitialized) {
            populator.addScript(new ClassPathResource("populate_db.sql"));
            populator.execute(dataSource);
        }
        isInitialized = true;
    }

    @AfterAll
    public static void clearDatabase() {
        populator.addScript(new ClassPathResource("clear_db.sql"));
        populator.execute(dataSource);
    }


    @Test
    public void shouldReturnBotOrderDto() {
        //GIVEN
        BotOrderEntity botOrderEntity = orderRepoService.findAll().get(0);
        //WHEN
        BotOrderDto dto = botOrderMapper.toDto(botOrderEntity);
        //THEN
        Assertions.assertEquals(botOrderEntity.getOrderId(), dto.getOrderId());
    }

    @Test
    public void shouldReturnBotDto() {
        // GIVEN
        String query = "Select b from BotEntity b JOIN FETCH b.botOrderEntities where b.botId =: id";
        BotEntity botEntity = entityManager
                .createQuery(query, BotEntity.class)
                .setParameter("id", 1)
                .getSingleResult();
        // WHEN
        BotDto dto = botMapper.toDto(botEntity);
        // THEN
        Assertions.assertEquals(botEntity.getBotId(), dto.getId());
        Assertions.assertNotNull(dto.getBotDataDto());
    }

    @Test
    public void shouldReturnListBotDto(){
        // GIVEN
        String query = "Select b from BotEntity b JOIN FETCH b.botOrderEntities";
        // WHEN
        TypedQuery<BotEntity> result = entityManager.createQuery(query, BotEntity.class);
        List<BotEntity> entitytList = result.getResultList();
        List<BotDto> dtoList = botMapper.toDtoList(entitytList);
        // THEN
        Assertions.assertEquals(entitytList.size(), dtoList.size());
    }
}
