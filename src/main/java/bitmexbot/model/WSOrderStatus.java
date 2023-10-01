package bitmexbot.model;

import bitmexbot.entity.BitmexOrder;
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
    private List<BitmexOrder> bitmexOrder;
}