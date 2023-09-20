package bitmexbot.repository;

import bitmexbot.entity.BitmexBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BotRepo extends JpaRepository<BitmexBot, Long> {

    @Transactional
    Optional<BitmexBot> findByBotId(int botId);

    @Transactional
    void deleteByBotId(int id);

    @Transactional
    @Query("SELECT b FROM BitmexBot b " +
            "JOIN b.bitmexBotData bd " +
            "JOIN b.bitmexOrders bo " +
            "WHERE bo.orderId = :orderId")
    Optional<BitmexBot> findBotByBitmexOrder(@Param("orderId") String orderId);

    @Transactional
    @Query("SELECT b FROM BitmexBot b LEFT JOIN FETCH b.bitmexOrders")
    Optional<List<BitmexBot>> findBotsWithOrders();

    @Modifying
    @Transactional
    default void updateBot(BitmexBot bitmexBot) {
        this.saveAndFlush(bitmexBot);
    }

    @Transactional
    default Optional<BitmexBot> createBot(BitmexBot bitmexBot) {
        return Optional.of(this.save(bitmexBot));
    }
}
