package ru.checkdev.notification.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

class PersonDTOTest {

    private PersonDTO person;

    @BeforeEach
    public void setUp() {
        List<RoleDTO> roles = new ArrayList<>();
        roles.add(new RoleDTO(1));
        Calendar created = Calendar.getInstance();
        person = new PersonDTO("email", "password", true, roles, created);
    }

    @Test
    public void testGetEmail() {
        assertThat("email", is(person.getEmail()));
    }

    @Test
    public void testGetPassword() {
        assertThat("password", is(person.getPassword()));
    }

    @Test
    public void testGetPrivacy() {
        assertThat(true, is(person.isPrivacy()));
    }

    @Test
    public void testGetRoles() {
        List<RoleDTO> roles = new ArrayList<>();
        roles.add(new RoleDTO(1));
        assertThat(roles, is(person.getRoles()));
    }

    @Test
    public void testGetCreated() {
        Calendar created = Calendar.getInstance();
        assertThat(created, is(person.getCreated()));
    }
}