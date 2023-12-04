package ru.job4j.site.component.safety;

import org.junit.jupiter.api.Test;
import ru.job4j.site.dto.WisherApprovedDTO;

import static org.assertj.core.api.Assertions.assertThat;

public class WisherApprovedDtoInspectorTest {

    private final WisherApprovedDtoInspector inspector =
            new WisherApprovedDtoInspector(new StringEraseXssInspector());

    @Test
    void whenTryToInsertBadScript() {
        var dangerApprovedWisher = new WisherApprovedDTO(1, 2, 3,
                "<script>alert('interview title')</script>",
                "<script>alert('interview link')</script>",
                "<script>alert('contact by')</script>");
        var defusedApprovedWisher = new WisherApprovedDTO(1, 2, 3,
                "scriptalert('interview title')script",
                "scriptalert('interview link')script",
                "scriptalert('contact by')script");
        assertThat(inspector.defuse(dangerApprovedWisher)).isEqualTo(defusedApprovedWisher);
    }
}
