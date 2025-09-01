package com.icando.ItemShop.repository;

import com.icando.ItemShop.entity.Item;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ItemRepositoryImpl implements ItemRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Item fin
}
