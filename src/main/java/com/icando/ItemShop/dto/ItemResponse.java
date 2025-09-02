package com.icando.ItemShop.dto;

import com.icando.ItemShop.entity.Item;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ItemResponse {

    private String name;
    private String imageUrl;
    private int quantity;
    private int point;

    public ItemResponse(Item item) {
        this.name = item.getName();
        this.imageUrl = item.getImageUrl();
        this. quantity = item.getQuantity();
        this. point = item.getPoint();
    }

    public static ItemResponse of (Item item){
        return new ItemResponse(item);
    }

}
