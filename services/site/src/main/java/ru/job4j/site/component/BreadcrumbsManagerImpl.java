package ru.job4j.site.component;

import org.springframework.stereotype.Component;
import ru.job4j.site.domain.Breadcrumb;

import java.util.ArrayList;
import java.util.List;

@Component
public class BreadcrumbsManagerImpl implements BreadcrumbsManager<Breadcrumb, List<Breadcrumb>> {

    @Override
    public List<Breadcrumb> initBreadcrumbs(Breadcrumb breadcrumb) {
        return List.of(breadcrumb);
    }

    @Override
    public List<Breadcrumb> addBreadcrumb(List<Breadcrumb> list, Breadcrumb breadcrumb) {
        List<Breadcrumb> result = list != null ? list : new ArrayList<>();
        result.add(breadcrumb);
        return result;
    }

    @Override
    public List<Breadcrumb> replaceBreadcrumbsWithCurrent(
            List<Breadcrumb> list, Breadcrumb currentBreadcrumb) {
        if (list != null) {
            int currentIndex = list.indexOf(currentBreadcrumb);
            if (currentIndex != -1) {
                list.subList(currentIndex + 1, list.size()).clear();
            } else {
                addBreadcrumb(list, currentBreadcrumb);
            }
        }
        return list;
    }
}