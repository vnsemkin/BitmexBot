package bitmexbot.entity;

import bitmexbot.config.Strategy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bots_data")
public class BitmexBotData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bitmex_key")
    private String key;
    @Column(name = "bitmex_secret")
    private String userName;
    private String userEmail;
    private int userAccount;
    private String userCurrency;
    private String secret;
    private Double step;
    private Integer level;
    private Double coefficient;
    private double lastBuy;
    private double lastSell;
    @Enumerated(EnumType.STRING)
    private Strategy strategy;
}
