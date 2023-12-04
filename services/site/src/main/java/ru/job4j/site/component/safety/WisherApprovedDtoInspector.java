package ru.job4j.site.component.safety;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.job4j.site.dto.WisherApprovedDTO;

@Component
public class WisherApprovedDtoInspector implements Inspector<WisherApprovedDTO> {

    private final Inspector<String> fieldInspector;

    public WisherApprovedDtoInspector(@Qualifier("stringEraseXssInspector")
                                              Inspector<String> fieldInspector) {
        this.fieldInspector = fieldInspector;
    }

    @Override
    public WisherApprovedDTO defuse(WisherApprovedDTO wisherApproved) {
        if (wisherApproved != null) {
            wisherApproved.setInterviewTitle(fieldInspector.defuse(wisherApproved.getInterviewTitle()));
            wisherApproved.setInterviewLink(fieldInspector.defuse(wisherApproved.getInterviewLink()));
            wisherApproved.setContactBy(fieldInspector.defuse(wisherApproved.getContactBy()));
        }
        return wisherApproved;
    }
}
