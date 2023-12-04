package ru.job4j.site.component.safety;


import org.junit.jupiter.api.Test;
import ru.job4j.site.dto.TopicDTO;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

public class TopicDtoXSSInspectorTest {

    private final TopicDtoXSSInspector inspector =
            new TopicDtoXSSInspector(new StringEraseXssInspector());

    @Test
    void whenTryToInsertBadScript() {
        var danderTopic = new TopicDTO();
        danderTopic.setName("<script>alert('name')</script>");
        danderTopic.setText("<script>alert('text')</script>");
        danderTopic.setCreated(Calendar.getInstance());
        danderTopic.setUpdated(Calendar.getInstance());
        danderTopic.setTotal(11);
        danderTopic.setCategory(null);
        danderTopic.setPosition(0);
        danderTopic.setCountInterview(11L);
        var defusedTopic = new TopicDTO();
        defusedTopic.setName("scriptalert('name')script");
        defusedTopic.setText("scriptalert('text')script");
        defusedTopic.setCreated(danderTopic.getCreated());
        defusedTopic.setUpdated(danderTopic.getUpdated());
        defusedTopic.setTotal(11);
        defusedTopic.setCategory(null);
        defusedTopic.setPosition(0);
        defusedTopic.setCountInterview(11L);
        assertThat(inspector.defuse(danderTopic)).isEqualTo(defusedTopic);
    }
}
