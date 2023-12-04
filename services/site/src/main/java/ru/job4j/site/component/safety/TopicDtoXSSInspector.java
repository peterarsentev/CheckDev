package ru.job4j.site.component.safety;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.job4j.site.dto.TopicDTO;

@Component
public class TopicDtoXSSInspector implements Inspector<TopicDTO> {

    private final Inspector<String> fieldInspector;

    public TopicDtoXSSInspector(@Qualifier("stringEraseXssInspector")
                                        Inspector<String> fieldInspector) {
        this.fieldInspector = fieldInspector;
    }

    @Override
    public TopicDTO defuse(TopicDTO topic) {
        if (topic != null) {
            topic.setName(fieldInspector.defuse(topic.getName()));
            topic.setText(fieldInspector.defuse(topic.getText()));
        }
        return topic;
    }
}
