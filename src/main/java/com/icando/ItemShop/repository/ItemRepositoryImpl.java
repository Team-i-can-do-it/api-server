package com.icando.ItemShop.repository;

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
public class ItemRepositoryImpl implements ItemRepositoryCustom {  // ItemRepositoryCustom 구현

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Item> getItemByPrice(String sortCondition) {  // 메서드명 수정
        QItem qItem = QItem.item;
        QPointShopHistory qHistory = QPointShopHistory.pointShopHistory;

        if ("가격 높은순".equals(sortCondition)) {
            return queryFactory
                    .selectFrom(qItem)
                    .orderBy(qItem.point.desc())
                    .fetch();
        }

        if ("가격 낮은순".equals(sortCondition)) {
            return queryFactory
                    .selectFrom(qItem)
                    .orderBy(qItem.point.asc())
                    .fetch();
        }

        if ("판매순".equals(sortCondition)) {
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

