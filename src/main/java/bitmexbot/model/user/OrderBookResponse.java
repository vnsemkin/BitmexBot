package bitmexbot.model.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderBookResponse implements BitmexData {
    private List<OrderInfo> orderInfoList;
}
