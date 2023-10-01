package ru.checkdev.mock.domain;

import lombok.*;
import javax.persistence.*;

@Entity(name = "wisher")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Builder(builderMethodName = "of")
@AllArgsConstructor
@NoArgsConstructor
public class Wisher {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @JoinColumn(name="user_id")
    private int userId;

    @JoinColumn(name="contact_by")
    private String contactBy;

    @JoinColumn(name="approve")
    private boolean approve;
}
