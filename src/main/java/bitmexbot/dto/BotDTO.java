package bitmexbot.dto;

import bitmexbot.entity.BitmexBot;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.entity.BitmexOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class BotDTO {
    private Integer id;
    private BitmexBotData bitmexBotData;
    private Set<BitmexOrder> bitmexOrders;

    public BotDTO of(BitmexBot bitmexBot) {
        BotDTO botDTO = new BotDTO();
        botDTO.setId(bitmexBot.getBotId());
        botDTO.setBitmexBotData(bitmexBot.getBitmexBotData());
        botDTO.setBitmexOrders(bitmexBot.getBitmexOrders());
        return botDTO;
    }

}

