package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.port.out.VisibilityRepository;
import fr.insee.pearljam.domain.organizationunit.port.out.OrganizationUnitRepository;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.campaign.port.in.CommunicationTemplateService;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitUpdateService;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStateClient;
import fr.insee.pearljam.domain.surveyunit.port.out.*;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.OrganizationUnitDB;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.*;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyUnitServiceImplTest {

    @Mock
    private SurveyUnitRepository surveyUnitRepository;

    @Mock
    private SurveyUnitTempZoneRepository surveyUnitTempZoneRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private StateRepository stateRepository;

    @Mock
    private InterviewerRepository interviewerRepository;

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private OrganizationUnitRepository organizationUnitRepository;

    @Mock
    private VisibilityRepository visibilityRepository;

    @Mock
    private ClosingCauseRepository closingCauseRepository;

    @Mock
    private UserService userService;

    @Mock
    private QuestionnaireStateClient questionnaireStateClient;

    @Mock
    private SurveyUnitUpdateService surveyUnitUpdateService;

    @Mock
    private CommunicationTemplateService communicationTemplateService;

    @Mock
    private DateService dateService;

    @Mock
    private JsonMapper jsonMapper;

    private SurveyUnitServiceImpl service;

    private static final String SURVEY_UNIT_ID = "SU-001";
    private static final String CAMPAIGN_ID = "CAMPAIGN-001";
    private static final String OU_ID = "OU-001";

    @BeforeEach
    void setUp() {
        service = new SurveyUnitServiceImpl(
                surveyUnitRepository,
                surveyUnitTempZoneRepository,
                addressRepository,
                stateRepository,
                interviewerRepository,
                campaignRepository,
                organizationUnitRepository,
                visibilityRepository,
                closingCauseRepository,
                userService,
                questionnaireStateClient,
                surveyUnitUpdateService,
                communicationTemplateService,
                dateService,
                jsonMapper
        );
    }

    private SurveyUnitDB buildTestSurveyUnit() {
        CampaignDB campaign = new CampaignDB();
        campaign.setId(CAMPAIGN_ID);
        
        OrganizationUnitDB ou = new OrganizationUnitDB();
        ou.setId(OU_ID);
        
        SurveyUnitDB surveyUnit = new SurveyUnitDB();
        surveyUnit.setId(SURVEY_UNIT_ID);
        surveyUnit.setCampaign(campaign);
        surveyUnit.setOrganizationUnit(ou);
        return surveyUnit;
    }

    private SurveyUnitDB buildTestSurveyUnitWithContactOutcome(ContactOutcomeType type) {
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        
        ContactOutcomeDB contactOutcome = new ContactOutcomeDB();
        contactOutcome.setType(type);
        contactOutcome.setSurveyUnit(surveyUnit);
        contactOutcome.setDate(new Date().getTime());
        contactOutcome.setTotalNumberOfContactAttempts(1);
        
        surveyUnit.setContactOutcome(contactOutcome);
        return surveyUnit;
    }

    // ==================== addStateAuto method tests ====================

    @Test
    void addStateAuto_should_add_TBR_state_when_contact_outcome_is_INA() throws Exception {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnitWithContactOutcome(ContactOutcomeType.INA);
        ClosingCauseDB closingCause = new ClosingCauseDB();
        surveyUnit.setClosingCause(closingCause);
        
        // When
        Method method = SurveyUnitServiceImpl.class
                .getDeclaredMethod("addStateAuto", SurveyUnitDB.class);
        method.setAccessible(true);
        method.invoke(service, surveyUnit);

        // Then
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository).save(stateCaptor.capture());
        
        StateDB savedState = stateCaptor.getValue();
        assertThat(savedState.getType()).isEqualTo(StateType.TBR);
        assertThat(savedState.getSurveyUnit()).isEqualTo(surveyUnit);
        assertThat(savedState.getDate()).isNotNull();
        
        assertThat(surveyUnit.getClosingCause()).isNull();
    }

    @Test
    void addStateAuto_should_add_FIN_state_when_contact_outcome_is_not_INA() throws Exception {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnitWithContactOutcome(ContactOutcomeType.REF);
        ClosingCauseDB closingCause = new ClosingCauseDB();
        surveyUnit.setClosingCause(closingCause);
        
        // When
        Method method = SurveyUnitServiceImpl.class
                .getDeclaredMethod("addStateAuto", SurveyUnitDB.class);
        method.setAccessible(true);
        method.invoke(service, surveyUnit);

        // Then
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository).save(stateCaptor.capture());
        
        StateDB savedState = stateCaptor.getValue();
        assertThat(savedState.getType()).isEqualTo(StateType.FIN);
        assertThat(savedState.getSurveyUnit()).isEqualTo(surveyUnit);
        assertThat(savedState.getDate()).isNotNull();
        
        assertThat(surveyUnit.getClosingCause()).isNull();
    }

    @Test
    void addStateAuto_should_add_FIN_state_when_contact_outcome_is_null() throws Exception {
        // Given
        SurveyUnitDB surveyUnit = buildTestSurveyUnit();
        surveyUnit.setContactOutcome(null);
        ClosingCauseDB closingCause = new ClosingCauseDB();
        surveyUnit.setClosingCause(closingCause);
        
        // When
        Method method = SurveyUnitServiceImpl.class
                .getDeclaredMethod("addStateAuto", SurveyUnitDB.class);
        method.setAccessible(true);
        method.invoke(service, surveyUnit);

        // Then
        ArgumentCaptor<StateDB> stateCaptor = ArgumentCaptor.forClass(StateDB.class);
        verify(stateRepository).save(stateCaptor.capture());
        
        StateDB savedState = stateCaptor.getValue();
        assertThat(savedState.getType()).isEqualTo(StateType.FIN);
        assertThat(savedState.getSurveyUnit()).isEqualTo(surveyUnit);
        assertThat(savedState.getDate()).isNotNull();
        
        assertThat(surveyUnit.getClosingCause()).isNull();
    }
}