package com.uuhnaut69.example.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TransferMoneyDTO {

  @NotBlank private String from;

  @NotBlank private String to;

  @NotNull private BigDecimal amount;
}
