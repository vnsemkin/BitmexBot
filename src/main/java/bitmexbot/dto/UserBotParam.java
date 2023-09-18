package bitmexbot.dto;

import bitmexbot.config.Strategy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBotParam {
    @NotBlank
    private String key;
    @NotBlank
    private String secret;
    @NotBlank
    private Double step;
    @NotBlank
    @Size(min = 1, max = 10)
    private Integer level;
    @NotBlank
    private Double coefficient;
    @NotBlank
    private Strategy strategy;
}
