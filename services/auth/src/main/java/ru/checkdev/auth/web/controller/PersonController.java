package ru.checkdev.auth.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.checkdev.auth.domain.Person;
import ru.checkdev.auth.service.PersonService;
import ru.checkdev.auth.service.RoleService;

import javax.servlet.ServletException;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

/**
 * @author parsentev
 * @since 30.09.2016
 */
@RestController
@RequestMapping("/person")
public class PersonController {
    private final StandardPasswordEncoder encoding = new StandardPasswordEncoder();
    private final PersonService persons;
    private final RoleService roles;

    private final String access;

    @Autowired
    public PersonController(final PersonService persons, RoleService roles, @Value("${access.key}") final String access) {
        this.persons = persons;
        this.roles = roles;
        this.access = access;
    }

    @GetMapping("/current")
    public Person getCurrent(Principal user) {
        return this.persons.findByEmail(user.getName()).get();
    }

    @PostMapping("/hh")
    public Person getPersonFromHh(@RequestBody String linc) {
        return persons.loadFromHh(linc);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/list/{limit}/{page}/{search}")
    public ResponseEntity<HashMap<String, Object>> getAll(@PathVariable int limit, @PathVariable int page, @PathVariable String search, Principal user) throws ServletException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        if ("noSearch".equals(search)) {
            data.put("users", this.persons.findAll(PageRequest.of(page, limit, Sort.Direction.ASC, "email")));
        } else {
            data.put("users", this.persons.findBySearch(search, PageRequest.of(page, limit, Sort.Direction.ASC, "email")));
        }
        data.put("total", this.persons.total());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<HashMap<String, Object>> get(@PathVariable int id, Principal user) throws ServletException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("person", this.persons.findById(id));
        data.put("roles", this.roles.findAll());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public Person loadProfile(@RequestParam String key) throws ServletException {
        return this.persons.findByKey(key);
    }

    @PostMapping("/by")
    public List<Person> getIn(@RequestBody List<String> keys, @RequestParam String key) throws ServletException {
        List<Person> result = Collections.emptyList();
        if (this.access.equals(key)) {
            result = this.persons.getIn(keys);
        }
        return result;
    }

    @PostMapping("/by/email")
    public Person findByEmail(@RequestParam String email, @RequestParam String key) throws ServletException {
        if (this.access.equals(key)) {
            return this.persons.findByEmail(email).orElseGet(Person::new);
        }
        throw new IllegalStateException();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/")
    public Person save(@RequestBody Person person, Principal user) throws ServletException {
        this.persons.saveRole(person);
        return person;
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/updateMultipart")
    public ResponseEntity<String> update(@RequestPart Person person, @RequestPart(required = false) MultipartFile file, Principal user) throws ServletException, IOException {
        String email = ((Map<String, String>) ((OAuth2Authentication) user)
                .getUserAuthentication().getDetails()).get("username");
        person.setEmail(email);
        return new ResponseEntity<>(persons.update(email, file, person), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public Object createPerson(@RequestBody Person person, Principal user) {
        Optional<Person> result = persons.create(person);
        return result.<Object>map(per -> new Object() {
            public Person getPerson() {
                return per;
            }
        }).orElseGet(() -> new Object() {
            public String getError() {
                return String.format("Пользователь с почтой %s уже существует.", person.getEmail());
            }
        });
    }

    @PutMapping("/changePassword")
    public void changePassword(@RequestBody Person person, Principal user) {
        String email = ((Map<String, String>) ((OAuth2Authentication) user)
                .getUserAuthentication().getDetails()).get("username");
        Person personDb = persons.findById(person.getId());
        if (email.equals(personDb.getEmail())) {
            personDb.setPassword(this.encoding.encode(person.getPassword()));
            persons.save(personDb);
        }
    }


    @PutMapping("/pass")
    public ResponseEntity<String> changePass(@RequestBody Person.Password password) {
        Person personDb = persons.findById(password.getId());
        String response;
        if (this.encoding.matches(password.getPassword(), personDb.getPassword())) {
            personDb.setPassword(this.encoding.encode(password.getNewPass()));
            persons.save(personDb);
            response = "ok";
        } else {
            response = "Введите правильный пароль";
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/random")
    public ResponseEntity<Map<String, Object>> showRandomPerson() {
        Map<String, Object> map = new HashMap<>();
        map.put("resume", this.persons.findByShow(true, 5));
        map.put("countResume", this.persons.showed());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/resume/{limit}/{page}")
    public ResponseEntity<Map<String, Object>> getResume(@PathVariable int limit, @PathVariable int page) {
        final int pageToShow = page == 0 ? page : page - 1;
        Map<String, Object> map = new HashMap<>();
        map.put("personsShowed", persons.findByShow(true, PageRequest.of(pageToShow, limit)));
        map.put("getTotal", persons.showed());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
