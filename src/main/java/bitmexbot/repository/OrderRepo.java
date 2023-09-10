package bitmexbot.repository;

import bitmexbot.entity.BitmexOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface OrderRepo extends JpaRepository<BitmexOrder, Long> {
    BitmexOrder findByOrderId(String ordId);
}