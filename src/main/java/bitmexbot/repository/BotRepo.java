package bitmexbot.repository;

import bitmexbot.entity.BitmexBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BotRepo extends JpaRepository<BitmexBot, Long> {

    Optional<BitmexBot> findByBotId(int botId);

    void removeByBotId(int id);

    @Query("SELECT b FROM BitmexBot b " +
            "JOIN b.bitmexBotData bd " +
            "JOIN b.bitmexOrders bo " +
            "WHERE bo.orderId = :orderId")
    Optional<BitmexBot> findBotByBitmexOrder(@Param("orderId") String orderId);

    @Query("SELECT b FROM BitmexBot b LEFT JOIN FETCH b.bitmexOrders")
    List<BitmexBot> findAllBotWithOrders();
}
