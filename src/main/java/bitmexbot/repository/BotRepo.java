package bitmexbot.repository;

import bitmexbot.entity.BitmexBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface BotRepo extends JpaRepository<BitmexBot, Long> {

    @Transactional
    BitmexBot findByBotId(int botId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BitmexBot bb WHERE bb.botId =:botId")
    void deleteByBotId(@Param("botId") int botId);

    @Transactional
    @Query("SELECT b FROM BitmexBot b " +
            "JOIN b.bitmexBotData bd " +
            "JOIN b.bitmexOrders bo " +
            "WHERE bo.orderId = :orderId")
    Optional<BitmexBot> findBotByBitmexOrder(@Param("orderId") String orderId);

    @Transactional
    default BitmexBot updateBot(BitmexBot bitmexBot) {
        return this.saveAndFlush(bitmexBot);
    }

    @Transactional
    default BitmexBot createBot(BitmexBot bitmexBot) {
        return this.save(bitmexBot);
    }
}
