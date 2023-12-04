package ru.job4j.site.component.safety;

import org.junit.jupiter.api.Test;
import ru.job4j.site.dto.WisherNotifyDTO;

import static org.assertj.core.api.Assertions.assertThat;

public class WisherNotifyDtoXSSInspectorTest {

    private final WisherNotifyDtoXSSInspector inspector =
            new WisherNotifyDtoXSSInspector(new StringEraseXssInspector());

    @Test
    void whenTryToInsertBadScript() {
        var danderWisherNotify = new WisherNotifyDTO();
        danderWisherNotify.setInterviewId(1);
        danderWisherNotify.setInterviewTitle("<script>alert('interview title')</script>");
        danderWisherNotify.setSubmitterId(2);
        danderWisherNotify.setUserId(3);
        danderWisherNotify.setUserName("<script>alert('user name')</script>");
        danderWisherNotify.setContactBy("<script>alert('contact by')</script>");
        var defusedWisherNotify = new WisherNotifyDTO();
        defusedWisherNotify.setInterviewId(1);
        defusedWisherNotify.setInterviewTitle("scriptalert('interview title')script");
        defusedWisherNotify.setSubmitterId(2);
        defusedWisherNotify.setUserId(3);
        defusedWisherNotify.setUserName("scriptalert('user name')script");
        defusedWisherNotify.setContactBy("scriptalert('contact by')script");
        assertThat(inspector.defuse(danderWisherNotify)).isEqualTo(defusedWisherNotify);
    }
}
