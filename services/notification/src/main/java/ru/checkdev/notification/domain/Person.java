package ru.checkdev.notification.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

/**
 * @author parsentev
 * @since 25.09.2016
 */

public class Person {

    private int id;

    private String username;

    private String email;

    private String key;

    private String password;

    private boolean active;

    private String experience;

    private boolean show;

    private String salary;
    private String aboutShort;

    private String about;

    private Photo photo;

    private List<Role> roles;

    /**
     * Privacy sign.
     */
    private boolean privacy;

    private String brief;

    private String urlHh;

    private String location;

    private Calendar updated;

    public Person() {
    }

    public Person(String username, String email, String password, boolean privacy) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.privacy = privacy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getAboutShort() {
        return aboutShort;
    }

    public void setAboutShort(String aboutShort) {
        this.aboutShort = aboutShort;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUrlHh() {
        return urlHh;
    }

    public void setUrlHh(String urlHh) {
        this.urlHh = urlHh;
    }

    /**
     * Return privacy sign.
     *
     * @return privacy sign.
     */
    public boolean isPrivacy() {
        return privacy;
    }

    /**
     * Set privacy sign.
     *
     * @param privacy privacy sign.
     */
    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return id == person.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Calendar getUpdated() {
        return updated;
    }

    public void setUpdated(Calendar updated) {
        this.updated = updated;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Password {
        private int id;
        private String password;
        private String newPass;

        public Password(int id, String password, String newPass) {
            this.id = id;
            this.password = password;
            this.newPass = newPass;
        }

        public Password() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNewPass() {
            return newPass;
        }

        public void setNewPass(String newPass) {
            this.newPass = newPass;
        }
    }
}
