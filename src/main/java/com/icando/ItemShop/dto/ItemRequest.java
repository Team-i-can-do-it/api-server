package com.icando.ItemShop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequest {

    @NotBlank(message = "상품명은 필수 입니다")
    private String name;

    @NotNull(message = "상품 이미지는 필수입니다")
    private MultipartFile imageUrl;

    @Min(value = 1, message = "상품의 개수는 최소 1개 이상이어야 합니다")
    private int quantity;

    @Min(value = 10, message = "상품 포인트는 최소 10포인트 이상이어야 합니다")
    private int point;

}
