package com.icando.ItemShop.dto;

import com.icando.ItemShop.entity.PointShopHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PointShopHistoryResponse {

    private LocalDateTime createdAt;
    private String name;
    private String imageUrl;
    private int point;

    public PointShopHistoryResponse(PointShopHistory pointShopHistory) {
        this.createdAt = pointShopHistory.getCreatedAt();
        this.name = pointShopHistory.getItem().getName();
        this.imageUrl = pointShopHistory.getItem().getImageUrl();
        this.point = pointShopHistory.getItem().getPoint();
    }
}
