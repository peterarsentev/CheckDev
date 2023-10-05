package ru.checkdev.desc.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.checkdev.desc.domain.Category;
import ru.checkdev.desc.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoryService {
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
        var list = new ArrayList<Category>();
        categoryRepository.findMostPopular().forEach(list::add);
        return list;
    }

    public void updateStatistic(int id) {
        categoryRepository.updateStatistic(id);
    }
}
