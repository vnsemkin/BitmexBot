package bitmexbot.dto;

import bitmexbot.entity.BitmexOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BitmexOrderDto {
    private String orderId;
    private String symbol;
    private double price;
    private String side;
    private double orderQty;
    private String ordType;
    private String ordStatus;
    private double filledPrice;

    public static BitmexOrderDto of(BitmexOrder bitmexOrder) {
        BitmexOrderDto bitmexOrderDTO = new BitmexOrderDto();
        bitmexOrderDTO.setOrderId(bitmexOrder.getOrderId());
        bitmexOrderDTO.setSymbol(bitmexOrder.getSymbol());
        bitmexOrderDTO.setPrice(bitmexOrder.getPrice());
        bitmexOrderDTO.setSide(bitmexOrder.getSide());
        bitmexOrderDTO.setOrderQty(bitmexOrder.getOrderQty());
        bitmexOrderDTO.setOrdType(bitmexOrder.getOrdType());
        bitmexOrderDTO.setOrdStatus(bitmexOrder.getOrdStatus());
        bitmexOrderDTO.setFilledPrice(bitmexOrder.getFilledPrice());

        return bitmexOrderDTO;
    }
}

