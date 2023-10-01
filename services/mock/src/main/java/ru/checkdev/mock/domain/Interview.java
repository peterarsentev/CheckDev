package ru.checkdev.mock.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity(name = "interview")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Builder(builderMethodName = "of")
@AllArgsConstructor
@NoArgsConstructor
public class Interview {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "Id must be non null")
    private int id;

    @NotNull(message = "Title must be non null")
    @JoinColumn(name="type_interview")
    private int typeInterview;

    @NotNull(message = "Title must be non null")
    @JoinColumn(name="submitter_id")
    private int submitterId;

    @NotBlank(message = "Title must be not empty")
    @JoinColumn(name="title")
    private String title;

    @NotBlank(message = "Title must be not empty")
    @JoinColumn(name="description")
    private String description;

    @NotBlank(message = "Title must be not empty")
    @JoinColumn(name="contact_by")
    private String contactBy;

    @NotBlank(message = "Title must be not empty")
    @JoinColumn(name="approximate_date")
    private String approximateDate;
}
