package bitmex.bitmexspring.repository.jpa;

import bitmex.bitmexspring.entity.BitmexBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface BotRepo extends JpaRepository<BitmexBot, Long> {
    void deleteByBotId(int id);

    // Custom query to find a BitmexBot by orderId in its BitmexOrders
    @Query("SELECT b FROM BitmexBot b " +
            "JOIN b.bitmexBotData bd " +
            "JOIN b.bitmexOrders bo " +
            "WHERE bo.orderId = :orderId")
    Optional<BitmexBot> findBotByBitmexOrder(@Param("orderId") String orderId);

    default BitmexBot updateBot(BitmexBot bitmexBot) {
        return this.saveAndFlush(bitmexBot);
    }

    @Transactional
    default BitmexBot createBot(BitmexBot bitmexBot) {
        return this.save(bitmexBot);
    }
}
