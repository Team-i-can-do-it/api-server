package com.icando.ItemShop.repository;

import com.icando.ItemShop.dto.ItemGetType;
import com.icando.ItemShop.entity.Item;
import com.icando.ItemShop.entity.QItem;
import com.icando.ItemShop.entity.QPointShopHistory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Item> getItemByPrice(ItemGetType itemGetType) {
        QItem qItem = QItem.item;
        QPointShopHistory qHistory = QPointShopHistory.pointShopHistory;

        if (ItemGetType.EXPENSIVE == itemGetType) {
            return queryFactory
                    .selectFrom(qItem)
                    .orderBy(qItem.point.desc())
                    .fetch();
        }

        if (ItemGetType.CHEAP == itemGetType) {
            return queryFactory
                    .selectFrom(qItem)
                    .orderBy(qItem.point.asc())
                    .fetch();
        }

        if (ItemGetType.POPULAR == itemGetType) {
            return queryFactory
                    .select(qItem)
                    .from(qItem)
                    .leftJoin(qHistory).on(qHistory.item.eq(qItem))
                    .groupBy(qItem.id)
                    .orderBy(qHistory.count().desc())
                    .fetch();
        }

        return queryFactory.selectFrom(qItem).fetch();
    }
}

