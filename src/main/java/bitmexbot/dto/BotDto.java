package bitmexbot.dto;

import bitmexbot.entity.BitmexBot;
import bitmexbot.entity.BitmexBotData;
import bitmexbot.entity.BitmexOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
public class BotDto {
    private Integer id;
    private BotDtoData botDtoData;
    private Set<BitmexOrderDto> bitmexOrderDtos;

    public static BotDto of(BitmexBot bitmexBot) {
        BotDto botDTO = new BotDto();
        botDTO.setId(bitmexBot.getBotId());

        // Create a new instance of BitmexBotDtoData and map the fields from BitmexBotData
        BitmexBotData bitmexBotData = bitmexBot.getBitmexBotData();
        BotDtoData botDtoData = new BotDtoData();
        botDtoData.setUserName(bitmexBotData.getUserName());
        botDtoData.setUserEmail(bitmexBotData.getUserEmail());
        botDtoData.setUserAccount(bitmexBotData.getUserAccount());
        botDtoData.setUserCurrency(bitmexBotData.getUserCurrency());
        botDtoData.setStep(bitmexBotData.getStep());
        botDtoData.setLevel(bitmexBotData.getLevel());
        botDtoData.setCoefficient(bitmexBotData.getCoefficient());
        botDtoData.setLastBuy(bitmexBotData.getLastBuy());
        botDtoData.setLastSell(bitmexBotData.getLastSell());
        botDtoData.setStrategy(bitmexBotData.getStrategy());

        botDTO.setBotDtoData(botDtoData);
        botDTO.setBitmexOrderDtos(orderDTOSetFromBitmexOrderSet(bitmexBot.getBitmexOrders()));

        return botDTO;
    }

    private static Set<BitmexOrderDto> orderDTOSetFromBitmexOrderSet(Set<BitmexOrder> orders){
        return orders.stream().map(BitmexOrderDto::of).collect(Collectors.toSet());
    }
}

