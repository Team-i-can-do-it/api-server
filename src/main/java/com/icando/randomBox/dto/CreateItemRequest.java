package com.icando.randomBox.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateItemRequest {

    @NotBlank(message = "상품명은 필수 입니다")
    private String item;

    @Min(value = 1, message = "상품의 개수는 최소 1개 이상이어야 합니다")
    private int quantity;

    @Positive(message = "확률은 0보다 커야 됩니다")
    @DecimalMax(value = "100.0", message = "확률은 100% 이하여야 합니다")
    private float probability;
}
