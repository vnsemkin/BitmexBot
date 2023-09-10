package bitmexbot.dto;

import bitmexbot.entity.BitmexOrder;
import bitmexbot.entity.BitmexBotData;
import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class BotDTO {
    private Long id;
    private BitmexBotData bitmexBotData;
    private Set<BitmexOrder> bitmexOrders;
}
