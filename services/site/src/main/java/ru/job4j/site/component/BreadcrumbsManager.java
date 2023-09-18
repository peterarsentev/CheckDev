package ru.job4j.site.component;

import ru.job4j.site.domain.Breadcrumb;

public interface BreadcrumbsManager<T extends Breadcrumb, S extends Iterable<T>> {

    S initBreadcrumbs(T breadcrumb);

    S addBreadcrumb(S list, T breadcrumb);

    S replaceBreadcrumbsWithCurrent(S list, T currentBreadcrumb);
}
