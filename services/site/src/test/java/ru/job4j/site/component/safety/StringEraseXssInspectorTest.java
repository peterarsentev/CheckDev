package ru.job4j.site.component.safety;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringEraseXssInspectorTest {

    private final StringEraseXssInspector inspector = new StringEraseXssInspector();

    @Test
    void whenEraseForbiddenSymbols() {
        String danderScript = "<script>alert('&&&')</script>";
        assertThat(inspector.defuse(danderScript)).isEqualTo("scriptalert('')script");
    }
}
