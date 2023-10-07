package bitmexbot.repository;

import bitmexbot.entity.BitmexOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepoService {
    private final OrderRepo orderRepo;

    @Autowired
    public OrderRepoService(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    public BitmexOrder findByOrderId(String ordId) {
        return orderRepo.findByOrderId(ordId).isPresent()
                ? orderRepo.findByOrderId(ordId).get() : new BitmexOrder();
    }
}
