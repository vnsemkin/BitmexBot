package bitmexbot.service.repo;

import bitmexbot.entity.BitmexOrder;
import bitmexbot.repository.OrderRepo;
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
