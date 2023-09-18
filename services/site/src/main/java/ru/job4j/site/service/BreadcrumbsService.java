package ru.job4j.site.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.site.component.BreadcrumbsManager;
import ru.job4j.site.domain.Breadcrumb;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BreadcrumbsService {

    private final BreadcrumbsManager<Breadcrumb, List<Breadcrumb>> breadcrumbsManager;

    public void addBreadCrumb(HttpSession session, Breadcrumb breadcrumb) {
        List<Breadcrumb> currentList = (List<Breadcrumb>) session.getAttribute("breadcrumbs");
        session.setAttribute("breadcrumbs", currentList == null
                ? breadcrumbsManager.initBreadcrumbs(breadcrumb)
                : breadcrumbsManager
                .replaceBreadcrumbsWithCurrent(new ArrayList<>(currentList), breadcrumb));
    }
}
