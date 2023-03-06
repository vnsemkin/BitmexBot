package bitmex.bitmexspring.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Order implements BitmexData {
    @JsonProperty("orderID")
    private String id;
    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("price")
    private double price;
    @JsonProperty("side")
    private String side;
    @JsonProperty("orderQty")
    private double orderQty;
    @JsonProperty("ordType")
    private String ordType;
    @JsonProperty("ordStatus")
    private String ordStatus;
}
