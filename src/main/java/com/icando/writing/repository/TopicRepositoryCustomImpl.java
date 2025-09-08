package com.icando.writing.repository;

import com.icando.writing.entity.QTopic;
import com.icando.writing.entity.Topic;
import com.icando.writing.enums.Category;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RequiredArgsConstructor
public class TopicRepositoryCustomImpl implements TopicRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 특정 카테고리 내에서 랜덤 주제를 조회합니다.
     */
    @Override
    public Optional<Topic> findRandomByCategory(Category category) {
        QTopic topic = QTopic.topic;
        return findRandomByPredicate(topic.category.eq(category));
    }

    /**
     * 모든 주제 중에서 랜덤 주제를 조회합니다.
     */
    @Override
    public Optional<Topic> findRandom() {
        // 별도 조건 없이(null) 공통 메서드 호출
        return findRandomByPredicate(null);
    }

    /**
     * 주어진 조건(Predicate)에 따라 랜덤 Topic을 조회하는 공통 로직
     * @param predicate 검색 조건 (nullable)
     * @return 조회된 Topic을 담은 Optional 객체
     */
    private Optional<Topic> findRandomByPredicate(BooleanExpression predicate) {
        QTopic topic = QTopic.topic;

        JPAQuery<Tuple> minMaxQuery = queryFactory
            .select(topic.Id.min(), topic.Id.max())
            .from(topic);

        if (predicate != null) {
            minMaxQuery.where(predicate);
        }

        Tuple minMaxId = minMaxQuery.fetchOne();

        // 조회 결과가 없을 경우
        if (minMaxId == null || minMaxId.get(topic.Id.min()) == null) {
            return Optional.empty();
        }

        Long minId = minMaxId.get(topic.Id.min());
        Long maxId = minMaxId.get(topic.Id.max());

        long randomId = ThreadLocalRandom.current().nextLong(minId, maxId + 1);

        JPAQuery<Topic> baseQuery = queryFactory.selectFrom(topic);
        if (predicate != null) {
            baseQuery.where(predicate);
        }

        Topic result = baseQuery.clone()
            .where(topic.Id.goe(randomId))
            .orderBy(topic.Id.asc())
            .fetchFirst();

        // 만약 없다면, 랜덤 ID보다 작은 ID 중에서 가장 가까운 데이터 조회 (Fallback)
        if (result == null) {
            result = baseQuery.clone()
                .where(topic.Id.loe(randomId))
                .orderBy(topic.Id.desc())
                .fetchFirst();
        }

        return Optional.ofNullable(result);
    }
}
