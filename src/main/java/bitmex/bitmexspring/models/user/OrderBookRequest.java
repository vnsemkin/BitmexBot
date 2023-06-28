package bitmex.bitmexspring.models.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderBookRequest implements BitmexData {
    private String symbol;
    private int depth;
}
