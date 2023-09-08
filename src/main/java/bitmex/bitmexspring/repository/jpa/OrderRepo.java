package bitmex.bitmexspring.repository.jpa;

import bitmex.bitmexspring.entity.BitmexOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<BitmexOrder, Long> {
    BitmexOrder findByOrderId(String ordId);
}