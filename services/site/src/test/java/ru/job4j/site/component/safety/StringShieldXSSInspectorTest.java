package ru.job4j.site.component.safety;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringShieldXSSInspectorTest {

    private final StringShieldXSSInspector inspector = new StringShieldXSSInspector();

    @Test
    void whenEraseForbiddenSymbols() {
        String danderScript = "<script>alert('&&&')</script>";
        assertThat(inspector.defuse(danderScript))
                .isEqualTo("&lt;script&gt;alert('&amp;&amp;&amp;')&lt;/script&gt;");
    }
}
