package bitmexbot.dto;

import bitmexbot.entity.BitmexBot;

import java.util.List;


public class BotDtoList {
    public static List<BotDto> of(List<BitmexBot> botList) {
        return botList.stream()
                .map(BotDto::of)
                .toList();
    }
}
