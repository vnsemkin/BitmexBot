package bitmexbot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BotOrderDto {
    private String orderId;
    private String symbol;
    private double price;
    private String side;
    private double orderQty;
    private String ordType;
    private String ordStatus;
    private double filledPrice;
}

