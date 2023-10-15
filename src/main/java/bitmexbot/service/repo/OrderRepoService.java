package bitmexbot.service.repo;

import bitmexbot.entity.BotOrderEntity;
import bitmexbot.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepoService {
    private final OrderRepo orderRepo;

    @Autowired
    public OrderRepoService(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    public BotOrderEntity findByOrderId(String ordId) {
        return orderRepo.findByOrderId(ordId).isPresent()
                ? orderRepo.findByOrderId(ordId).get() : new BotOrderEntity();
    }

    public List<BotOrderEntity> findAll(){
        return orderRepo.findAll();
    }
}
