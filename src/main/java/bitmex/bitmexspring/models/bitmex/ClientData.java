package bitmex.bitmexspring.models.bitmex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Controller
public class ClientData {
    private String userName;
    private String userEmail;
    private int userAccount;
    private String userCurrency;
    private String key;
    private String secret;
    private double step;
    private int level;
    private double coefficient;
    private double lastBuy;
    private double lastSell;
    private double filledPrice;
}
