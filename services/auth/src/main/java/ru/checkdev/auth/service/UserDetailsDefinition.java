package ru.checkdev.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.repository.PersonRepository;

import java.util.stream.Collectors;

@Component
public class UserDetailsDefinition implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsDefinition.class);

    private final PersonRepository persons;

    public UserDetailsDefinition(PersonRepository persons) {
        this.persons = persons;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) {
        Profile person = this.persons.findByEmail(email);
        if (person != null) {
            if (person.isActive()) {
                return new User(email,
                        person.getPassword(),
                        person.getRoles().stream()
                                .map(
                                        role -> new SimpleGrantedAuthority(role.getValue())
                                ).collect(Collectors.toList())
                ) {
                    public String getKey() {
                        return person.getKey();
                    }
                };
            } else {
                throw new DisabledException(String.format("Пользователь с почтой %s не активирован.", email));
            }
        } else {
            throw new DisabledException("Логин или пароль не совпадают.");
        }
    }
}
