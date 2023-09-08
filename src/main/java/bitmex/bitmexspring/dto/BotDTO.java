package bitmex.bitmexspring.dto;

import bitmex.bitmexspring.entity.BitmexOrder;
import bitmex.bitmexspring.entity.BitmexBotData;
import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class BotDTO {
    private Long id;
    private BitmexBotData bitmexBotData;
    private Set<BitmexOrder> bitmexOrders;
}
