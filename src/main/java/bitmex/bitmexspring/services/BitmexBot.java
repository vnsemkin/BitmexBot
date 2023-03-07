package bitmex.bitmexspring.services;

import bitmex.bitmexspring.models.bitmex.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:network.properties")
public class BitmexBot implements Runnable {
    private int id;
    private ClientData clientData;
    private final OrderPost orderPost;

    @Autowired
    public BitmexBot(OrderPost orderPost) {
        this.orderPost = orderPost;
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

    @Override
    public void run() {
        orderPost.setClientData(clientData);
        orderPost.initialBuy();
    }

    @Override
    public String toString() {
        return "BitmexBot id : " + id;
    }
}

