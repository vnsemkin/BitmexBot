package bitmexbot.entity;

import bitmexbot.model.user.BitmexData;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class BitmexOrder implements BitmexData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "bot_id")
    BitmexBot bitmexBot;
    @JsonProperty("orderID")
    @Column(name = "order_id")
    private String orderId;
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
    private double filledPrice;
}
