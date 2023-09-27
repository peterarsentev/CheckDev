package ru.job4j.site.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.job4j.site.dto.TopicDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private int id;
    private String name;
    private int total;
}
