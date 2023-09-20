package bitmexbot.dto;

import bitmexbot.config.Strategy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBotParamDTO {
    @NotBlank
    private String key;
    @NotBlank
    private String secret;
    private Double step;
    @Min(value = 1, message = "Level must be at least 1")
    @Max(value = 10, message = "Level cannot be greater than 10")
    private Integer level;
    private Double coefficient;
    private Strategy strategy;
}
