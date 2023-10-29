package ru.checkdev.auth.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.service.ProfileService;
import ru.checkdev.auth.service.RoleService;

import javax.servlet.ServletException;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author parsentev
 * @since 30.09.2016
 */
@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final StandardPasswordEncoder encoding = new StandardPasswordEncoder();
    private final ProfileService profileService;
    private final RoleService roles;

    @Autowired
    public ProfileController(final ProfileService profileService, RoleService roles) {
        this.profileService = profileService;
        this.roles = roles;
    }

    @GetMapping("/current")
    public Profile getCurrent(Principal user) {
        return this.profileService.findByEmail(user.getName()).get();
    }

    @PostMapping("/hh")
    public Profile getPersonFromHh(@RequestBody String linc) {
        return profileService.loadFromHh(linc);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/list/{limit}/{page}/{search}")
    public ResponseEntity<HashMap<String, Object>> getAll(@PathVariable int limit, @PathVariable int page, @PathVariable String search, Principal user) throws ServletException {
        HashMap<String, Object> data = new HashMap<>();
        if ("noSearch".equals(search)) {
            data.put("users", this.profileService.findAll(PageRequest.of(page, limit, Sort.Direction.ASC, "email")));
        } else {
            data.put("users", this.profileService.findBySearch(search, PageRequest.of(page, limit, Sort.Direction.ASC, "email")));
        }
        data.put("total", this.profileService.total());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<HashMap<String, Object>> get(@PathVariable int id, Principal user) throws ServletException {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("person", this.profileService.findById(id));
        data.put("roles", this.roles.findAll());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public Profile loadProfile(@RequestParam String key) throws ServletException {
        return this.profileService.findByKey(key);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/")
    public Profile save(@RequestBody Profile profile, Principal user) throws ServletException {
        this.profileService.saveRole(profile);
        return profile;
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/updateMultipart")
    public ResponseEntity<String> update(@RequestPart Profile profile, @RequestPart(required = false) MultipartFile file, Principal user) throws ServletException, IOException {
        String email = ((Map<String, String>) ((OAuth2Authentication) user)
                .getUserAuthentication().getDetails()).get("username");
        profile.setEmail(email);
        return new ResponseEntity<>(profileService.update(email, file, profile), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create")
    public Object createPerson(@RequestBody Profile profile, Principal user) {
        Optional<Profile> result = profileService.create(profile);
        return result.<Object>map(per -> new Object() {
            public Profile getProfile() {
                return per;
            }
        }).orElseGet(() -> new Object() {
            public String getError() {
                return String.format("Пользователь с почтой %s уже существует.", profile.getEmail());
            }
        });
    }

    @PutMapping("/changePassword")
    public void changePassword(@RequestBody Profile profile, Principal user) {
        String email = ((Map<String, String>) ((OAuth2Authentication) user)
                .getUserAuthentication().getDetails()).get("username");
        Profile profileDb = profileService.findById(profile.getId());
        if (email.equals(profileDb.getEmail())) {
            profileDb.setPassword(this.encoding.encode(profile.getPassword()));
            profileService.save(profileDb);
        }
    }

    @PutMapping("/pass")
    public ResponseEntity<String> changePass(@RequestBody Profile.Password password) {
        Profile profileDb = profileService.findById(password.getId());
        String response;
        if (this.encoding.matches(password.getPassword(), profileDb.getPassword())) {
            profileDb.setPassword(this.encoding.encode(password.getNewPass()));
            profileService.save(profileDb);
            response = "ok";
        } else {
            response = "Введите правильный пароль";
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/random")
    public ResponseEntity<Map<String, Object>> showRandomPerson() {
        Map<String, Object> map = new HashMap<>();
        map.put("resume", this.profileService.findByShow(true, 5));
        map.put("countResume", this.profileService.showed());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/resume/{limit}/{page}")
    public ResponseEntity<Map<String, Object>> getResume(@PathVariable int limit, @PathVariable int page) {
        final int pageToShow = page == 0 ? page : page - 1;
        Map<String, Object> map = new HashMap<>();
        map.put("personsShowed", profileService.findByShow(true, PageRequest.of(pageToShow, limit)));
        map.put("getTotal", profileService.showed());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
