package bitmexbot.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class BotDto {
    private Integer id;
    private BotDataDto botDataDto;
    private Set<BotOrderDto> botOrderDtoSet;
}

