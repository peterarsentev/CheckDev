package ru.checkdev.desc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.checkdev.desc.domain.Category;
import ru.checkdev.desc.repository.CategoryRepository;

import java.util.*;

@Service
@AllArgsConstructor
public class CategoryService {
    private final static int MOST_POPULAR = 5;
    private final CategoryRepository categoryRepository;

    public Optional<Category> findById(int categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public void delete(int categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    public void update(Category category) {
        categoryRepository.save(category);
    }

    public List<Category> getAll() {
        var list = new ArrayList<Category>();
        categoryRepository.findAllByOrderByTotalDesc().forEach(list::add);
        return list;
    }

    public List<Category> getMostPopular() {
        List<Category> rsl = new ArrayList<>();
        var list = getAll();
        Comparator<Category> comparator = Comparator.comparingInt(Category::getTotal);
        list.sort(comparator.reversed());
        for (int i = 0; i < MOST_POPULAR; i++) {
            rsl.add(list.get(i));
        }
        return rsl;
    }

    public void updateStatistic(int id) {
        categoryRepository.updateStatistic(id);
    }
}
