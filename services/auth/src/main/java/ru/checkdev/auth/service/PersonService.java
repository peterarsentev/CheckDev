package ru.checkdev.auth.service;


import com.google.common.collect.Lists;
import org.apache.commons.lang.RandomStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.checkdev.auth.domain.Notify;
import ru.checkdev.auth.domain.Person;
import ru.checkdev.auth.domain.Photo;
import ru.checkdev.auth.domain.Role;
import ru.checkdev.auth.repository.PersonRepository;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * @author parsentev
 * @since 25.09.2016
 */
@Service
public class PersonService {
    private final Logger log = LoggerFactory.getLogger(PersonService.class);
    private final PasswordEncoder encoding = new BCryptPasswordEncoder();
    private final PersonRepository persons;
    private final Messenger msg;

    @Autowired
    public PersonService(final PersonRepository persons, final Messenger msg) {
        this.persons = persons;
        this.msg = msg;
    }

    public Optional<Person> reg(Person person) {
        Optional<Person> result = Optional.empty();
        try {
            if (person.isPrivacy()) {
                person.setRoles(null);
                person.setActive(true);
                person.setKey(
                        this.encoding.encode(
                                String.format("%s%s", System.currentTimeMillis(), person.getPassword())
                        )
                );
                person.setPassword(this.encoding.encode(person.getPassword()));
                person.setUpdated(Calendar.getInstance());
                result = Optional.of(this.persons.save(person));
                Map<String, Object> keys = new HashMap<>();
                keys.put("key", person.getKey());
                this.msg.send(new Notify(person.getEmail(), keys, Notify.Type.REG.name()));
            }
        } catch (DataIntegrityViolationException e) {
            log.error("not unique email {}", person.getEmail());
        }
        return result;
    }

    public Optional<Person> create(Person person) {
        Optional<Person> result = Optional.empty();
        try {
            person.setPrivacy(true);
            person.setRoles(null);
            person.setKey(
                    this.encoding.encode(
                            String.format("%s%s", System.currentTimeMillis(), person.getPassword())
                    )
            );
            person.setPassword(this.encoding.encode(person.getPassword()));
            result = Optional.of(this.persons.save(person));
        } catch (DataIntegrityViolationException e) {
            log.error("not unique email {}", person.getEmail());
        }
        return result;
    }

    public Optional<Person> findByEmail(String email) {
        final Optional<Person> result;
        Person person = this.persons.findByEmail(email);
        if (person == null) {
            result = Optional.empty();
        } else {
            result = Optional.of(person);
        }
        return result;
    }

    public List<Person> getAll() {
        return Lists.newArrayList(persons.findAll());
    }

    public List<Person> getIn(List<String> keys) {
        return this.persons.findByKeyIn(keys);
    }

    @Transactional
    public boolean activated(String key) {
        Person person = this.persons.findByKey(key);
        boolean result = false;
        if (person != null && !person.isActive()) {
            person.setActive(true);
            this.persons.save(person);
            result = true;
        }
        return result;
    }

    public Optional<Person> forgot(Person person) {
        final Optional<Person> result;
        Person find = this.persons.findByEmail(person.getEmail());
        if (find == null) {
            result = Optional.empty();
        } else {
            String password = RandomStringUtils.randomAlphabetic(8);
            find.setPassword(this.encoding.encode(password));
            this.persons.save(find);
            Map<String, Object> keys = new HashMap<>();
            keys.put("password", password);
            this.msg.send(new Notify(person.getEmail(), keys, Notify.Type.FORGOT.name()));
            result = Optional.of(person);
        }
        return result;
    }

    public List<Person> findAll(Pageable pageable) {
        return this.persons.findAll(pageable).getContent();
    }

    public Long total() {
        return this.persons.total();
    }

    public Person findById(int id) {
        return this.persons.findById(id).get();
    }

    public void save(Person person) {
        this.persons.save(person);
    }

    @Transactional
    public void saveRole(Person person) {
        Person load = this.persons.findById(person.getId()).get();
        List<Role> roles = new ArrayList<>();
        for (Role role : person.getRoles()) {
            if (role != null) {
                roles.add(role);
            }
        }
        if (person.isActive()) {
            load.setActive(true);
        }
        load.setRoles(roles);
        load.setUpdated(Calendar.getInstance());
        this.persons.save(load);
    }

    public Person findByKey(String key) {
        return this.persons.findByKey(key);
    }

    public List<Person> findBySearch(String search, PageRequest email) {
        return this.persons.findByEmailContainingOrUsernameContaining(search, search, email).getContent();
    }

    public List<Person> findByShow(boolean show, int limit) {
        return this.persons.findByShow(show, PageRequest.of(0, limit));
    }

    public List<Person> findByShow(boolean show) {
        return this.persons.findByShow(show);
    }

    public List<Person> findByShow(boolean show, Pageable pageable) {
        return this.persons.findByShow(show, pageable);
    }

    public Long showed() {
        return this.persons.showed();
    }

    public Photo compress(MultipartFile multipartFile) {
        Photo photo = new Photo();
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(bos)) {
            BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = writers.next();
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.5f);
            writer.write(null, new IIOImage(bufferedImage, null, null), param);
            byte[] bytes = bos.toByteArray();
            writer.dispose();
            photo.setPhoto(bytes);
            photo.setName(multipartFile.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photo;
    }


    public Person loadFromHh(String linc) {
        Person person = null;
        try {
            Document doc = Jsoup.connect(linc).validateTLSCertificates(false).get();
            String name = doc.getElementsByAttributeValue("data-qa", "resume-personal-name").first().text();
            String address = doc.getElementsByAttributeValue("data-qa", "resume-personal-address").first().text();
            String experience = doc.getElementsByClass("resume-block__title-text resume-block__title-text_sub").first().text();
            String salary = doc.getElementsByAttributeValue("data-qa", "resume-block-salary").first().text();
            String aboutShort = doc.getElementsByAttributeValue("data-qa", "resume-block-title-position").first().text();
            StringBuilder about = new StringBuilder();
            String startP = "<p>";
            String endP = "</p>";
            Elements elements = doc.getElementsByAttributeValue("itemprop", "worksFor");
            for (Element element : elements) {
                String text = element.getElementsByClass("bloko-column bloko-column_s-2 bloko-column_m-2 bloko-column_l-2").first().text();
                String companyName = element.getElementsByClass("resume-block__sub-title").first().text();
                String description = element.getElementsByAttributeValue("data-qa", "resume-block-experience-description").first().text();
                about.append(startP).append(text).append(endP);
                about.append(startP).append(companyName).append(endP);
                about.append(startP).append(description).append(endP);
            }
            person = new Person(name, experience, salary, aboutShort, about.toString(), address);
            Element img = doc.getElementsByClass("resume-photo__image HH-Bloko-PopupSwitcher-Switcher").first();
            if (img != null) {
                String url = img.absUrl("src");
                person.setUrlHh(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return person;
    }


    public String update(String email, MultipartFile file, Person person) {
        String result = "ok";
        Person personDb = persons.findPerson(email);
        Photo photo;
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            if (file.getSize() < 1000000) {
                photo = compress(file);
                if (personDb.getPhoto() == null) {
                    personDb.setPhoto(photo);
                } else {
                    BeanUtils.copyProperties(photo, personDb.getPhoto(), "id");
                }
            } else {
                result = "Photo is very big!";
            }
        } else if (!StringUtils.isEmpty(person.getUrlHh())) {
            try {
                byte[] bytes = Jsoup.connect(person.getUrlHh()).validateTLSCertificates(false).ignoreContentType(true).execute().bodyAsBytes();
                String uuidFile = UUID.randomUUID().toString();
                photo = new Photo(bytes, uuidFile);
                if (personDb.getPhoto() == null) {
                    personDb.setPhoto(photo);
                } else {
                    BeanUtils.copyProperties(photo, personDb.getPhoto(), "id");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Не верная ссылка";
            }
        }
        Set<String> nullPropertyNames = getNullPropertyNames(person, "id", "email", "key", "roles", "privacy", "photo");
        BeanUtils.copyProperties(person, personDb, nullPropertyNames.toArray(new String[0]));
        personDb.setUpdated(Calendar.getInstance());
        personDb.setActive(true);
        persons.save(personDb);
        return result;
    }

    private Set<String> getNullPropertyNames(Object source, String... extra) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        emptyNames.addAll(Arrays.asList(extra));
        return emptyNames;
    }
}
