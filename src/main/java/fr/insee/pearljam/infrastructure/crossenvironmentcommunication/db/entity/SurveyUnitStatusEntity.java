package fr.insee.pearljam.infrastructure.crossenvironmentcommunication.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.insee.pearljam.api.domain.SurveyUnit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_unit_status")
@NoArgsConstructor
@Getter
@Setter
public class SurveyUnitStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(length = 50)
    private String id;

    @Column
    private String state;

    @Column(name = "date", insertable = false, updatable = false)
    private LocalDateTime date;

    @ManyToOne
    @JsonIgnore
    private SurveyUnit surveyUnit;
}