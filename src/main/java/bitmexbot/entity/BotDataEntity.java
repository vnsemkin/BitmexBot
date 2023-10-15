package bitmexbot.entity;

import bitmexbot.config.Strategy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bots_data")
public class BotDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "bitmex_key")
    private String key;
    @Column(name = "bitmex_secret")
    private String secret;
    private String userName;
    private String userEmail;
    private int userAccount;
    private String userCurrency;
    private Double step;
    private Integer level;
    private Double coefficient;
    private double lastBuy;
    private double lastSell;
    @Enumerated(EnumType.STRING)
    private Strategy strategy;
}
