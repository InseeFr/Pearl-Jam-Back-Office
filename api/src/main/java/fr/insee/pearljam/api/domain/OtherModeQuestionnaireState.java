package fr.insee.pearljam.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class OtherModeQuestionnaireState {
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