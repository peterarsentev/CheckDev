package ru.job4j.site.component.safety;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.job4j.site.dto.WisherNotifyDTO;

@Component
public class WisherNotifyDtoXSSInspector implements Inspector<WisherNotifyDTO> {

    private final Inspector<String> fieldInspector;

    public WisherNotifyDtoXSSInspector(@Qualifier("stringEraseXssInspector")
                                               Inspector<String> fieldInspector) {
        this.fieldInspector = fieldInspector;
    }

    @Override
    public WisherNotifyDTO defuse(WisherNotifyDTO wisherNotify) {
        if (wisherNotify != null) {
            wisherNotify.setInterviewTitle(fieldInspector.defuse(wisherNotify.getInterviewTitle()));
            wisherNotify.setUserName(fieldInspector.defuse(wisherNotify.getUserName()));
            wisherNotify.setContactBy(fieldInspector.defuse(wisherNotify.getContactBy()));
        }
        return wisherNotify;
    }
}
