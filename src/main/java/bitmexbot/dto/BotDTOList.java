package bitmexbot.dto;

import bitmexbot.entity.BitmexBot;

import java.util.List;


public class BotDTOList {
    public static List<BotDTO> of(List<BitmexBot> botList) {
        return botList.stream()
                .map(b -> new BotDTO().of(b))
                .toList();
    }

}
