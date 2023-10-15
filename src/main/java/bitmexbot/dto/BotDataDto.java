package bitmexbot.dto;

import bitmexbot.config.Strategy;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BotDataDto {
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
