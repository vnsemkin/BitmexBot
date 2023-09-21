package bitmexbot.dto;

import bitmexbot.config.Strategy;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBotParamDTO {
    @NotBlank
    @NotNull
    private String key;
    @NotBlank
    @NotNull
    private String secret;
    @NotNull
    private Double step;
    @NotNull
    @Min(value = 1, message = "Level must be at least 1")
    @Max(value = 10, message = "Level cannot be greater than 10")
    private Integer level;
    @NotNull
    private Double coefficient;
    @NotNull
    private Strategy strategy;

    @AssertTrue(message = "Coefficient must be more than 100 !")
    public boolean isCoefficientMoreThatOneHundred() {
        return coefficient > 100;
        //onto feature_21_09
        //new feature
    }
}
