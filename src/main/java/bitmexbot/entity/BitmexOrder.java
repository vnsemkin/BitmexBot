package bitmexbot.entity;

import bitmexbot.model.BitmexData;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class BitmexOrder implements BitmexData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
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
