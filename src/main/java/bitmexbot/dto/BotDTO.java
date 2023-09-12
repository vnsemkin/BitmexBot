package bitmexbot.dto;

import bitmexbot.entity.BitmexBotData;
import bitmexbot.entity.BitmexOrder;

import java.util.Set;



public record BotDTO(Integer id, BitmexBotData bitmexBotData, Set<BitmexOrder> bitmexOrders) {
}
