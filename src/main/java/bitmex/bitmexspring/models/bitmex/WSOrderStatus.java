package bitmex.bitmexspring.models.bitmex;

import bitmex.bitmexspring.models.user.BitmexData;
import bitmex.bitmexspring.models.user.Order;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class WSOrderStatus implements BitmexData {
    @JsonProperty("action")
    private String action;
    @JsonProperty("data")
    private List<Order> order;
}