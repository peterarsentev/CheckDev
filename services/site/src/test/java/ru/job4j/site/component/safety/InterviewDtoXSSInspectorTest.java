package ru.job4j.site.component.safety;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import ru.job4j.site.dto.InterviewDTO;

import static org.assertj.core.api.Assertions.assertThat;

@AllArgsConstructor
public class InterviewDtoXSSInspectorTest {

    private final InterviewDtoXSSInspector inspector =
            new InterviewDtoXSSInspector(new StringShieldXSSInspector());

    @Test
    void whenTryToInsertBadScript() {
        var danderInterview = new InterviewDTO(0, 1, 1,
                "<script>alert('danger')</script>",
                2, 3,
                "<script>alert('title')</script>",
                "<script>alert('additional')</script>",
                "<script>alert('contact by')</script>",
                "<script>alert('approximate date')</script>",
                "<script>alert('create date')</script>",
                4,
                "A & B",
                12L);
        var defusedInterview = new InterviewDTO(0, 1, 1,
                "&lt;script&gt;alert('danger')&lt;/script&gt;",
                2, 3,
                "&lt;script&gt;alert('title')&lt;/script&gt;",
                "&lt;script&gt;alert('additional')&lt;/script&gt;",
                "&lt;script&gt;alert('contact by')&lt;/script&gt;",
                "&lt;script&gt;alert('approximate date')&lt;/script&gt;",
                "&lt;script&gt;alert('create date')&lt;/script&gt;",
                4,
                "A &amp; B",
                12L);
        assertThat(inspector.defuse(danderInterview)).isEqualTo(defusedInterview);
    }
}
