package ru.job4j.site.component.safety;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.job4j.site.dto.PersonDTO;

@Component
public class PersonDtoXSSInspector implements Inspector<PersonDTO> {

    private final Inspector<String> fieldInspector;

    public PersonDtoXSSInspector(@Qualifier("stringEraseXssInspector")
                                         Inspector<String> fieldInspector) {
        this.fieldInspector = fieldInspector;
    }

    @Override
    public PersonDTO defuse(PersonDTO person) {
        if (person != null) {
            person.setUsername(fieldInspector.defuse(person.getUsername()));
            person.setEmail(fieldInspector.defuse(person.getEmail()));
            person.setExperience(fieldInspector.defuse(person.getExperience()));
            person.setSalary(fieldInspector.defuse(person.getSalary()));
            person.setLocation(fieldInspector.defuse(person.getLocation()));
        }
        return person;
    }
}
