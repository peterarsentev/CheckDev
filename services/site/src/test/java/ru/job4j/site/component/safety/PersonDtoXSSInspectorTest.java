package ru.job4j.site.component.safety;

import org.junit.jupiter.api.Test;
import ru.job4j.site.dto.PersonDTO;

import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonDtoXSSInspectorTest {

    private final PersonDtoXSSInspector inspector =
            new PersonDtoXSSInspector(new StringEraseXssInspector());

    @Test
    void whenTryToInsertBadScript() {
        var danderPerson = new PersonDTO();
        danderPerson.setUsername("<script>alert('username')</script>");
                danderPerson.setEmail("danger_person@checkdev.ru");
                danderPerson.setPassword("password_&</>");
                danderPerson.setExperience("<script>alert('experience')</script>");
                danderPerson.setSalary("<script>alert('salary')</script>");
                danderPerson.setPhoto(null);
                danderPerson.setLocation("<script>alert('location')</script>");
                danderPerson.setUpdated(Calendar.getInstance());
                danderPerson.setCreated(Calendar.getInstance());
        var defusedPerson = new PersonDTO();
        defusedPerson.setUsername("scriptalert('username')script");
        defusedPerson.setEmail("danger_person@checkdev.ru");
        defusedPerson.setPassword("password_&</>");
        defusedPerson.setExperience("scriptalert('experience')script");
        defusedPerson.setSalary("scriptalert('salary')script");
        defusedPerson.setPhoto(null);
        defusedPerson.setLocation("scriptalert('location')script");
        defusedPerson.setUpdated(danderPerson.getUpdated());
        defusedPerson.setCreated(danderPerson.getCreated());
        assertThat(inspector.defuse(danderPerson)).isEqualTo(defusedPerson);
    }
}
