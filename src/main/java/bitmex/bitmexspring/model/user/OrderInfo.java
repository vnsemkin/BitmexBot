package bitmex.bitmexspring.model.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfo {
    @JsonProperty("symbol")
    String symbol;
    @JsonProperty("side")
    String side;
    @JsonProperty("price")
    double price;
}
