package fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter;

import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.campaign.jpa.CampaignJpaRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InseeAddressDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.InterviewerDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.StateDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("auth")
@Transactional
class SurveyUnitAssignedDaoAdapterTest {

    @Autowired
    private SurveyUnitAssignedDaoAdapter adapter;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CampaignJpaRepository campaignRepository;

    @Autowired
    private InterviewerRepository interviewerRepository;

    @Autowired
    private SurveyUnitRepository surveyUnitRepository;

    @Autowired
    private StateRepository stateRepository;

    private static final String CAMPAIGN_1_ID= "CAMP-1";
    private static final String CAMPAIGN_2_ID= "CAMP-2";

    @BeforeEach
    void setup() {

        // =========================
        // CAMPAIGNS
        // =========================
        CampaignDB campaign1 = new CampaignDB(
            CAMPAIGN_1_ID,
            "Campaign 1",
            IdentificationConfiguration.HOUSEF2F,
            ContactOutcomeConfiguration.F2F,
            ContactAttemptConfiguration.F2F,
            "test@test.com",
            false,
            false
        );

        CampaignDB campaign2 = new CampaignDB(
            CAMPAIGN_2_ID,
            "Campaign 2",
            IdentificationConfiguration.HOUSEF2F,
            ContactOutcomeConfiguration.F2F,
            ContactAttemptConfiguration.F2F,
            "test@test.com",
            false,
            false
        );

        campaignRepository.saveAll(List.of(campaign1, campaign2));


        InterviewerDB jean = new InterviewerDB();
        jean.setId("INT-1");
        jean.setFirstName("Jean");
        jean.setLastName("Dupont");

        InterviewerDB marie = new InterviewerDB();
        marie.setId("INT-2");
        marie.setFirstName("Marie");
        marie.setLastName("Martin");

        InterviewerDB bernard = new InterviewerDB();
        bernard.setId("INT-3");
        bernard.setFirstName("Bernard");
        bernard.setLastName("Bernard");

        interviewerRepository.saveAll(List.of(jean, marie, bernard));

        // =========================
        // SURVEY UNITS
        // =========================

        createSurveyUnit(
            "SU-1",
            campaign1,
            jean,
            "75000 Paris",
            StateType.TBR
        );

        createSurveyUnit(
            "SU-2",
            campaign1,
            marie,
            "69000 Lyon",
            StateType.NVM
        );

        createSurveyUnit(
            "SU-3",
            campaign1,
            bernard,
            "33000 Bordeaux",
            StateType.INS
        );

        createSurveyUnit(
            "SU-4",
            campaign2,
            marie,
            "59000 Lille",
            StateType.TBR
        );

        createSurveyUnit(
            "SU-5",
            campaign2,
            marie,
            "",
            StateType.TBR
        );

        createSurveyUnit(
            "SU-6",
            campaign2,
            marie,
            "Lille",
            StateType.TBR
        );


        SurveyUnitDB su1 = surveyUnitRepository.findById("SU-1").orElseThrow();

        StateDB oldState = new StateDB();
        oldState.setSurveyUnit(su1);
        oldState.setType(StateType.NVM);
        oldState.setDate(LocalDateTime.now().minusDays(2).toEpochSecond(ZoneOffset.UTC));

        StateDB latestState = new StateDB();
        latestState.setSurveyUnit(su1);
        latestState.setType(StateType.TBR);
        latestState.setDate(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        stateRepository.save(oldState);
        stateRepository.save(latestState);

        entityManager.flush();
        entityManager.clear();
    }

    private void createSurveyUnit(
        String id,
        CampaignDB campaign,
        InterviewerDB interviewer,
        String address,
        StateType state
    ) {
        SurveyUnitDB su = new SurveyUnitDB();
        su.setId(id);
        su.setCampaign(campaign);
        su.setInterviewer(interviewer);

        InseeAddressDB addr = new InseeAddressDB();
        addr.setL6(address);
        entityManager.persist(addr);

        su.setAddress(addr);
        entityManager.persist(su);

        StateDB st = new StateDB();
        st.setSurveyUnit(su);
        st.setType(state);
        st.setDate(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        entityManager.persist(st);
    }

    @Test
    void should_return_survey_units_assigned() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<SurveyUnitAssigned> result =
            adapter.findSurveyUnitsAssigned(List.of(CAMPAIGN_1_ID), null, pageable);

        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    void should_search_by_survey_unit_id() {

        Page<SurveyUnitAssigned> result =
            adapter.findSurveyUnitsAssigned(
                List.of(CAMPAIGN_1_ID),
                "su-2",
                PageRequest.of(0, 20)
            );

        assertThat(result.getContent())
            .extracting(SurveyUnitAssigned::surveyUnitId)
            .containsExactly("SU-2");
    }

    @Test
    void should_search_by_city() {

        Page<SurveyUnitAssigned> result =
            adapter.findSurveyUnitsAssigned(
                List.of(CAMPAIGN_2_ID),
                "Lille",
                PageRequest.of(0, 20)
            );

        assertThat(result.getContent()).hasSize(2)
            .extracting(SurveyUnitAssigned::city)
            .containsExactly("Lille", "Lille");
    }

    @Test
    void should_return_only_survey_units_from_requested_campaign() {

        Page<SurveyUnitAssigned> result =
            adapter.findSurveyUnitsAssigned(
                List.of(CAMPAIGN_1_ID),
                null,
                PageRequest.of(0, 20)
            );

        assertThat(result.getContent())
            .extracting(SurveyUnitAssigned::surveyUnitId)
            .containsExactlyInAnyOrder(
                "SU-1",
                "SU-2",
                "SU-3"
            );

        assertThat(result.getContent())
            .extracting(SurveyUnitAssigned::surveyUnitId)
            .doesNotContain("SU-4");
    }

    @Test
    void should_return_latest_state() {

        Page<SurveyUnitAssigned> result =
            adapter.findSurveyUnitsAssigned(
                List.of(CAMPAIGN_1_ID),
                "SU-1",
                PageRequest.of(0, 20)
            );

        SurveyUnitAssigned su = result.getContent().getFirst();

        assertThat(su.questionnaireState())
            .isEqualTo(StateType.TBR.name());
    }

    @Test
    void should_search_by_interviewer_name() {

        Page<SurveyUnitAssigned> result =
            adapter.findSurveyUnitsAssigned(
                List.of(CAMPAIGN_1_ID),
                "marie martin",
                PageRequest.of(0, 20)
            );

        assertThat(result.getContent())
            .extracting(SurveyUnitAssigned::surveyUnitId)
            .containsExactly("SU-2");
    }

    @Test
    void should_filter_by_address() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<SurveyUnitAssigned> result =
            adapter.findSurveyUnitsAssigned(List.of(CAMPAIGN_1_ID), "paris", pageable);

        assertThat(result.getContent())
            .extracting(SurveyUnitAssigned::city)
            .contains("Paris");
    }

    static Stream<Arguments> sortFields() {
        return Stream.of(
            Arguments.of("surveyUnitId", (Function<SurveyUnitAssigned, String>) SurveyUnitAssigned::surveyUnitId),
            Arguments.of("surveyUnitDisplayName", (Function<SurveyUnitAssigned, String>) SurveyUnitAssigned::surveyUnitDisplayName),
            Arguments.of("interviewerLabel", (Function<SurveyUnitAssigned, String>) SurveyUnitAssigned::interviewerLastName),
            Arguments.of("ssech",(Function<SurveyUnitAssigned, String>)  SurveyUnitAssigned::ssech),
            Arguments.of("location", (Function<SurveyUnitAssigned, String>) SurveyUnitAssigned::location),
            Arguments.of("city", (Function<SurveyUnitAssigned, String>) SurveyUnitAssigned::city),
            Arguments.of("questionnaireState", (Function<SurveyUnitAssigned, String>) SurveyUnitAssigned::questionnaireState),
            Arguments.of("closingCause", (Function<SurveyUnitAssigned, String>) SurveyUnitAssigned::closingCause)
        );
    }

    @ParameterizedTest
    @MethodSource("sortFields")
    void should_sort_by_allowed_field(
        String sortField,
        Function<SurveyUnitAssigned, String> extractor) {

        Pageable pageable = PageRequest.of(
            0,
            20,
            Sort.by(sortField).ascending()
        );

        Page<SurveyUnitAssigned> result =
            adapter.findSurveyUnitsAssigned(List.of(CAMPAIGN_1_ID), null, pageable);

        List<String> values = result.getContent()
            .stream()
            .map(extractor)
            .filter(Objects::nonNull)
            .toList();

        assertThat(values).isSorted();
    }
}