package bitmex.bitmexspring.services;

import bitmex.bitmexspring.models.bitmex.*;
import bitmex.bitmexspring.models.user.Order;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

@Service
@Scope("prototype")
@PropertySource("classpath:network.properties")
public class BitmexBot implements Runnable {
    private int id;
    private ClientData clientData;
    private final OrderPost orderPost;
    private final List<Order> orderList;
    private ExecutorService executor;

    public List<Order> getOrderList() {
        return orderList;
    }

    public int getId() {
        return id;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public BitmexBot(OrderPost orderPost) {
        this.orderPost = orderPost;
        orderList = new CopyOnWriteArrayList<>();
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public ClientData getClientData(){
        return clientData;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void cancelOrders(){
        orderPost.delete(orderList);
    }

    @Override
    public void run() {
        orderPost.setClientData(clientData);
        orderPost.initialBuy(this);
    }

    @Override
    public String toString() {
        return "BitmexBot id : "
                + id + " : "
                + orderList;
    }
}

