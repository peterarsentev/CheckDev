package ru.job4j.site.component.safety;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.job4j.site.dto.InterviewDTO;

@Component
public class InterviewDtoXSSInspector implements Inspector<InterviewDTO> {

    private final Inspector<String> fieldInspector;

    public InterviewDtoXSSInspector(@Qualifier("stringShieldXSSInspector")
                                            Inspector<String> fieldInspector) {
        this.fieldInspector = fieldInspector;
    }

    @Override
    public InterviewDTO defuse(InterviewDTO interview) {
        if (interview != null) {
            interview.setStatusInfo(fieldInspector.defuse(interview.getStatusInfo()));
            interview.setTitle(fieldInspector.defuse(interview.getTitle()));
            interview.setAdditional(fieldInspector.defuse(interview.getAdditional()));
            interview.setContactBy(fieldInspector.defuse(interview.getContactBy()));
            interview.setApproximateDate(fieldInspector.defuse(interview.getApproximateDate()));
            interview.setCreateDate(fieldInspector.defuse(interview.getCreateDate()));
            interview.setAuthor(fieldInspector.defuse(interview.getAuthor()));
        }
        return interview;
    }
}
