package ru.checkdev.desc.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.checkdev.desc.domain.Category;
import ru.checkdev.desc.utility.Utility;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
    Iterable<Category> findAllByOrderByTotalDesc();

    @Query(value = "SELECT * FROM cd_category ORDER BY total DESC LIMIT "
            + Utility.LIMIT_MOST_POPULAR, nativeQuery = true)
    Iterable<Category> findMostPopular();

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Modifying
    @Query("UPDATE cd_category c SET total = total + 1 WHERE c.id = :id")
    void updateStatistic(int id);
}
