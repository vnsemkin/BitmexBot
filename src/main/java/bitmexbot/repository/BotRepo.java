package bitmexbot.repository;

import bitmexbot.entity.BotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BotRepo extends JpaRepository<BotEntity, Long> {

    Optional<BotEntity> findByBotId(int botId);

    void removeByBotId(int id);

    @Query("SELECT b FROM BotEntity b " +
            "JOIN b.botDataEntity bd " +
            "JOIN b.botOrderEntities bo " +
            "WHERE bo.orderId = :orderId")
    Optional<BotEntity> findBotByBotOrder(String orderId);

    @Query("SELECT b FROM BotEntity b LEFT JOIN FETCH b.botOrderEntities")
    List<BotEntity> findAllBotWithOrders();
}
