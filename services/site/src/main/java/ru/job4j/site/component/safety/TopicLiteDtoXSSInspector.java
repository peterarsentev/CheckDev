package ru.job4j.site.component.safety;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.job4j.site.dto.TopicLiteDTO;

@Component
public class TopicLiteDtoXSSInspector implements Inspector<TopicLiteDTO> {

    private final Inspector<String> fieldInspector;

    public TopicLiteDtoXSSInspector(@Qualifier("stringEraseXssInspector")
                                            Inspector<String> fieldInspector) {
        this.fieldInspector = fieldInspector;
    }

    @Override
    public TopicLiteDTO defuse(TopicLiteDTO topicLite) {
        if (topicLite != null) {
            topicLite.setName(fieldInspector.defuse(topicLite.getName()));
            topicLite.setText(fieldInspector.defuse(topicLite.getText()));
            topicLite.setCategoryName(fieldInspector.defuse(topicLite.getCategoryName()));
        }
        return topicLite;
    }
}
