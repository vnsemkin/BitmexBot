package bitmexbot.repository;

import bitmexbot.entity.BitmexOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepo extends JpaRepository<BitmexOrder, Long> {
    Optional<BitmexOrder> findByOrderId(String ordId);
}