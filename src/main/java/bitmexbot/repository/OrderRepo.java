package bitmexbot.repository;

import bitmexbot.entity.BotOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepo extends JpaRepository<BotOrderEntity, Long> {
    Optional<BotOrderEntity> findByOrderId(String ordId);
}