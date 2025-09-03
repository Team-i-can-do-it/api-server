package com.icando.ItemShop.repository;


import com.icando.ItemShop.dto.ItemGetType;
import com.icando.ItemShop.entity.Item;

import java.util.List;

public interface ItemRepositoryCustom {
    List<Item> getItemByPrice(ItemGetType itemGetType);
}
