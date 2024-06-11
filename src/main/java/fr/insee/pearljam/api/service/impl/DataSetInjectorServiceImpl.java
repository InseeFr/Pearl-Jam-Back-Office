package fr.insee.pearljam.api.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.ClosingCause;
import fr.insee.pearljam.api.domain.ClosingCauseType;
import fr.insee.pearljam.api.domain.Comment;
import fr.insee.pearljam.api.domain.CommentType;
import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcome;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.api.domain.Identification;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.domain.InseeAddress;
import fr.insee.pearljam.api.domain.InseeSampleIdentifier;
import fr.insee.pearljam.api.domain.Interviewer;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.api.domain.OrganizationUnitType;
import fr.insee.pearljam.api.domain.Person;
import fr.insee.pearljam.api.domain.PhoneNumber;
import fr.insee.pearljam.api.domain.Source;
import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.domain.Title;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.domain.Visibility;
import fr.insee.pearljam.api.domain.IdentificationQuestions.AccessQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.CategoryQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.IdentificationQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.OccupantQuestionValue;
import fr.insee.pearljam.api.domain.IdentificationQuestions.SituationQuestionValue;
import fr.insee.pearljam.api.repository.AddressRepository;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.IdentificationRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.PersonRepository;
import fr.insee.pearljam.api.repository.SampleIdentifierRepository;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.repository.VisibilityRepository;
import fr.insee.pearljam.api.service.DataSetInjectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class DataSetInjectorServiceImpl implements DataSetInjectorService {

	private final EntityManagerFactory emf;

	private final CampaignRepository campaignRepository;
	private final AddressRepository addressRepository;
	private final OrganizationUnitRepository organizationUnitRepository;
	private final InterviewerRepository interviewerRepository;
	private final SampleIdentifierRepository sampleIdentifierRepository;
	private final UserRepository userRepository;
	private final VisibilityRepository visibilityRepository;
	private final SurveyUnitRepository surveyUnitRepository;
	private final PersonRepository personRepository;
	private final IdentificationRepository identificationRepository;

	private static final String PHONE_NUMBERS = "+33677542802";

	public HttpStatus createDataSet() {

		LocalDateTime now = LocalDateTime.now();
		long p1Month = toMilliseconds(now.plusMonths(1));
		long p2Months = toMilliseconds(now.plusMonths(2));
		long m1Day = toMilliseconds(now.minusDays(1));
		long m2Days = toMilliseconds(now.minusDays(2));
		long m3Days = toMilliseconds(now.minusDays(3));
		long m4Days = toMilliseconds(now.minusDays(4));
		long m5Days = toMilliseconds(now.minusDays(5));
		long m6Days = toMilliseconds(now.minusDays(6));
		long m7Days = toMilliseconds(now.minusDays(7));

		if (!campaignRepository.findAllIds().isEmpty()) {
			log.info("The database already contains a campaign, the dataset was not imported");
			return HttpStatus.NOT_MODIFIED;
		}
		log.info("Dataset creation start");

		final String FRANCE = "France";
		// create address entities

		InseeAddress address1 = addressRepository
				.save(new InseeAddress("Ted Farmer", "", "", "1 rue de la gare", "", "29270 Carhaix", FRANCE,
						true, "Bat. C", "Etg 4", "Porte 48", "Escalier B", true));
		InseeAddress address2 = addressRepository
				.save(new InseeAddress("Cecilia Ortega", "", "", "2 place de la mairie", "", "90000 Belfort",
						FRANCE, false, null, null, null, null, false));
		InseeAddress address3 = addressRepository
				.save(new InseeAddress("Claude Watkins", "", "", "3 avenue de la République", "",
						"32230 Marciac", FRANCE, false, null, null, null, null, false));
		InseeAddress address4 = addressRepository
				.save(new InseeAddress("Veronica Gill", "", "", "4 chemin du ruisseau", "", "44190 Clisson",
						FRANCE, false, null, null, null, null, false));
		InseeAddress address5 = addressRepository
				.save(new InseeAddress("Christine Aguilar", "", "", "5 rue de l'école", "",
						"59620 Aulnoye-Aimeries", FRANCE, false, null, null, null, null, false));
		InseeAddress address6 = addressRepository
				.save(new InseeAddress("Louise Walker", "", "", "6 impasse du lac", "", "38200 Vienne",
						FRANCE, false, null, null, null, null, false));
		InseeAddress address7 = addressRepository
				.save(new InseeAddress("Anthony Bennett", "", "", "7 avenue de la Liberté", "", "62000 Arras",
						FRANCE, false, null, null, null, null, false));
		InseeAddress address8 = addressRepository
				.save(new InseeAddress("Christopher Lewis", "", "", "8 route du moulin", "", "35000 Rennes",
						FRANCE, false, null, null, null, null, false));
		InseeAddress address9 = addressRepository
				.save(new InseeAddress("Laurent Neville", "", "", "5 route du sapin", "", "35000 Rennes",
						FRANCE, false, null, null, null, null, false));

		// create organization_unit entities
		OrganizationUnit ouNational = organizationUnitRepository.save(
				new OrganizationUnit("OU-NATIONAL", "National organizational unit", OrganizationUnitType.NATIONAL));
		OrganizationUnit ouNorth = organizationUnitRepository
				.save(new OrganizationUnit("OU-NORTH", "North region organizational unit", OrganizationUnitType.LOCAL));
		OrganizationUnit ouSouth = organizationUnitRepository
				.save(new OrganizationUnit("OU-SOUTH", "South region organizational unit", OrganizationUnitType.LOCAL));
		OrganizationUnit ouWest = organizationUnitRepository
				.save(new OrganizationUnit("OU-WEST", "West region organizational unit", OrganizationUnitType.LOCAL));

		ouNorth.setOrganizationUnitParent(ouNational);
		ouSouth.setOrganizationUnitParent(ouNational);
		ouWest.setOrganizationUnitParent(ouNational);

		// create interviewer entities

		Interviewer intw1 = interviewerRepository.save(new Interviewer("INTW1", "Margie", "Lucas",
				"margie.lucas@ou.com", "+3391231231230", null, Title.MISS));
		Interviewer intw2 = interviewerRepository.save(new Interviewer("INTW2", "Carlton", "Campbell",
				"carlton.campbell@ou.com", "+3391231231231", null, Title.MISTER));
		Interviewer intw3 = interviewerRepository.save(new Interviewer("INTW3", "Gerald", "Edwards",
				"gerald.edwards@ou.com", "+3391231231232", null, Title.MISTER));
		Interviewer intw4 = interviewerRepository.save(new Interviewer("INTW4", "Melody", "Grant",
				"melody.grant@ou.com", "+3391231231233", null, Title.MISS));

		// create sample_identifier entities
		InseeSampleIdentifier sampleId1 = sampleIdentifierRepository
				.save(new InseeSampleIdentifier(11, "1", 11, 11, 11, 11, 1, 11, 11, "11", "11"));
		InseeSampleIdentifier sampleId2 = sampleIdentifierRepository
				.save(new InseeSampleIdentifier(12, "1", 12, 12, 12, 12, 1, 12, 12, "12", "12"));
		InseeSampleIdentifier sampleId3 = sampleIdentifierRepository
				.save(new InseeSampleIdentifier(13, "1", 13, 13, 13, 13, 2, 13, 13, "13", "13"));
		InseeSampleIdentifier sampleId4 = sampleIdentifierRepository
				.save(new InseeSampleIdentifier(14, "1", 14, 14, 14, 14, 3, 14, 14, "14", "14"));
		InseeSampleIdentifier sampleId5 = sampleIdentifierRepository
				.save(new InseeSampleIdentifier(20, "2", 20, 20, 20, 20, 1, 20, 20, "20", "20"));
		InseeSampleIdentifier sampleId6 = sampleIdentifierRepository
				.save(new InseeSampleIdentifier(21, "2", 21, 21, 21, 21, 1, 21, 21, "21", "21"));
		InseeSampleIdentifier sampleId7 = sampleIdentifierRepository
				.save(new InseeSampleIdentifier(22, "2", 22, 22, 22, 22, 2, 22, 22, "22", "22"));
		InseeSampleIdentifier sampleId8 = sampleIdentifierRepository
				.save(new InseeSampleIdentifier(23, "2", 23, 23, 23, 23, 1, 23, 23, "23", "23"));
		InseeSampleIdentifier sampleId9 = sampleIdentifierRepository
				.save(new InseeSampleIdentifier(24, "2", 24, 24, 24, 24, 1, 24, 24, "24", "24"));

		// create user entities
		userRepository.save(new User("ABC", "Melinda", "Webb", ouNorth));
		userRepository.save(new User("DEF", "Everett", "Juste", ouNorth));
		User user3 = userRepository.save(new User("GHI", "Elsie", "Clarke", ouSouth));
		User user4 = userRepository.save(new User("JKL", "Julius", "Howell", ouNational));
		userRepository.save(new User("MNO", "Ted", "Kannt", ouWest));

		String campLabel = "Everyday life and health survey 2021";
		// create campaign entities
		Campaign campaign1 = campaignRepository
				.save(new Campaign("SIMPSONS2020X00", "Survey on the Simpsons tv show 2020",
						IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F,
						ContactAttemptConfiguration.F2F, "first.email@test.com", Boolean.FALSE));
		Campaign campaign2 = campaignRepository.save(new Campaign("VQS2021X00", campLabel,
				IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.TEL, ContactAttemptConfiguration.TEL,
				"second.email@test.com", Boolean.FALSE));
		Campaign campaign3 = campaignRepository.save(new Campaign("ZCLOSEDX00", campLabel,
				IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.F2F, ContactAttemptConfiguration.F2F,
				"third.email@test.com", Boolean.FALSE));
		Campaign campaign4 = campaignRepository.save(new Campaign("XCLOSEDX00", campLabel,
				IdentificationConfiguration.IASCO, ContactOutcomeConfiguration.TEL, ContactAttemptConfiguration.TEL,
				"fourth.email@test.com", Boolean.FALSE));

		// create preference entities : User/Campaign relation
		user3.setCampaigns(List.of(campaign1));
		user4.setCampaigns(List.of(campaign1, campaign2));

		// create visibility entities

		// opened visibilities
		visibilityRepository.save(new Visibility(m4Days, m3Days, m2Days, m1Day, p1Month, p2Months, campaign1, ouNorth));
		visibilityRepository.save(new Visibility(m4Days, m3Days, m2Days, m1Day, p1Month, p2Months, campaign2, ouNorth));
		visibilityRepository.save(new Visibility(m4Days, m3Days, m2Days, m1Day, p1Month, p2Months, campaign2, ouSouth));
		visibilityRepository.save(new Visibility(m4Days, m3Days, m2Days, m1Day, p1Month, p2Months, campaign1, ouSouth));

		// closed visibilities
		visibilityRepository.save(new Visibility(m7Days, m6Days, m5Days, m4Days, m3Days, m1Day, campaign3, ouSouth));
		visibilityRepository.save(new Visibility(m7Days, m6Days, m5Days, m4Days, m3Days, m1Day, campaign3, ouWest));
		visibilityRepository.save(new Visibility(m7Days, m6Days, m5Days, m4Days, m3Days, m1Day, campaign4, ouSouth));

		// create survey_unit entities

		SurveyUnit su11 = surveyUnitRepository
				.save(new SurveyUnit("11", true, false, address1, sampleId1, campaign1, intw1));
		su11.setCampaign(campaign1);
		su11.setOrganizationUnit(ouNorth);
		SurveyUnit su12 = surveyUnitRepository
				.save(new SurveyUnit("12", true, false, address2, sampleId2, campaign1, intw1));
		su12.setCampaign(campaign1);
		su12.setOrganizationUnit(ouNorth);
		SurveyUnit su13 = surveyUnitRepository
				.save(new SurveyUnit("13", false, false, address3, sampleId3, campaign1, intw2));
		su13.setCampaign(campaign1);
		su13.setOrganizationUnit(ouNorth);
		SurveyUnit su14 = surveyUnitRepository
				.save(new SurveyUnit("14", false, false, address4, sampleId4, campaign1, intw3));
		su14.setCampaign(campaign1);
		su14.setOrganizationUnit(ouNorth);
		SurveyUnit su20 = surveyUnitRepository
				.save(new SurveyUnit("20", false, false, address5, sampleId5, campaign2, intw1));
		su20.setCampaign(campaign2);
		su20.setOrganizationUnit(ouNorth);
		SurveyUnit su21 = surveyUnitRepository
				.save(new SurveyUnit("21", false, false, address6, sampleId6, campaign2, intw2));
		su21.setCampaign(campaign2);
		su21.setOrganizationUnit(ouNorth);
		SurveyUnit su22 = surveyUnitRepository
				.save(new SurveyUnit("22", false, false, address7, sampleId7, campaign2, intw4));
		su22.setCampaign(campaign2);
		su22.setOrganizationUnit(ouNorth);
		SurveyUnit su23 = surveyUnitRepository
				.save(new SurveyUnit("23", false, false, address8, sampleId8, campaign2, intw4));
		su23.setCampaign(campaign2);
		su23.setOrganizationUnit(ouNorth);
		SurveyUnit su24 = surveyUnitRepository
				.save(new SurveyUnit("24", true, false, address9, sampleId9, campaign1, null));
		su24.setCampaign(campaign1);
		su24.setOrganizationUnit(ouNorth);

		// create person entities
		String email = "test@test.com";
		Person person1 = personRepository
				.save(new Person(Title.MISTER, "Ted", "Farmer", email, true, true, 11111111l, su11));
		Person person2 = personRepository
				.save(new Person(Title.MISS, "Cecilia", "Ortega", email, true, true, 11111111l, su12));
		Person person3 = personRepository
				.save(new Person(Title.MISTER, "Claude", "Watkins", email, true, true, 11111111l, su13));
		Person person4 = personRepository
				.save(new Person(Title.MISS, "Veronica", "Baker", email, true, true, 11111111l, su14));
		Person person5 = personRepository
				.save(new Person(Title.MISS, "Christine", "Aguilar", email, true, false, 11111111l, su11));
		Person person6 = personRepository
				.save(new Person(Title.MISS, "Louise", "Walker", email, true, false, 11111111l, su11));
		Person person7 = personRepository
				.save(new Person(Title.MISTER, "Anthony", "Bennett", email, true, false, 11111111l, su12));
		Person person8 = personRepository
				.save(new Person(Title.MISTER, "Christopher", "Lewis", email, true, false, 11111111l, su14));
		Person person9 = personRepository
				.save(new Person(Title.MISS, "Harriette", "Raymond", email, true, true, 11111111l, su20));
		Person person10 = personRepository
				.save(new Person(Title.MISTER, "Aimée", "Lamothe", email, true, true, 11111111l, su21));
		Person person11 = personRepository
				.save(new Person(Title.MISTER, "Perrin", "Blanchard", email, true, true, 11111111l, su22));
		Person person12 = personRepository
				.save(new Person(Title.MISTER, "Artus", "Arnoux", email, true, true, 11111111l, su23));
		Person person13 = personRepository
				.save(new Person(Title.MISTER, "Laurent", "Neville", email, true, true, 11111111l, su24));

		// create phone_number entities
		person1.setPhoneNumbers(Set.of(new PhoneNumber(Source.FISCAL, true, PHONE_NUMBERS, person1),
				new PhoneNumber(Source.FISCAL, false, PHONE_NUMBERS, person1)));
		addPhoneNUmber(person2);
		addPhoneNUmber(person3);
		addPhoneNUmber(person4);
		addPhoneNUmber(person5);
		addPhoneNUmber(person6);
		addPhoneNUmber(person7);
		addPhoneNUmber(person8);
		addPhoneNUmber(person9);
		addPhoneNUmber(person10);
		addPhoneNUmber(person11);
		addPhoneNUmber(person12);
		addPhoneNUmber(person13);

		// create state entities
		su11.setStates(Set.of(new State(111112111l, su11, StateType.VIN),
				new State(110111111l, su11, StateType.NNS),
				new State(101111111l, su11, StateType.TBR)));
		su12.setStates(Set.of(new State(111112111l, su12, StateType.TBR)));
		su13.setStates(Set.of(new State(111112111l, su13, StateType.TBR)));
		su14.setStates(Set.of(new State(111112111l, su14, StateType.TBR)));
		su20.setStates(Set.of(new State(111112111l, su20, StateType.VIC)));
		su21.setStates(Set.of(new State(111112111l, su21, StateType.VIC)));
		su22.setStates(Set.of(new State(111112111l, su22, StateType.FIN)));
		su23.setStates(Set.of(new State(111112111l, su23, StateType.VIC)));
		su24.setStates(Set.of(new State(111112111l, su24, StateType.TBR)));
		// create contact_outcome entities

		su24.setContactOucome(new ContactOutcome(null, 1590504478334l, ContactOutcomeType.DUK, null, su24));
		// create comment entities
		su13.setComments(Set.of(new Comment(null, CommentType.INTERVIEWER, "un commentaire", su13)));
		// create closing_cause entities
		su11.setClosingCause(new ClosingCause(null, m3Days, ClosingCauseType.NPI, su11));

		// create identification entities
		Identification identif1 = identificationRepository
				.save(new Identification(null, IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
						SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY,
						OccupantQuestionValue.IDENTIFIED, su11));
		Identification identif2 = identificationRepository
				.save(new Identification(null, IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
						SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY,
						OccupantQuestionValue.IDENTIFIED, su21));

		su11.setIdentification(identif1);
		su21.setIdentification(identif2);

		log.info("Dataset creation end");
		return HttpStatus.OK;

	}

	public HttpStatus deleteDataSet() {
		try (InputStream sqlFileInputStream = getClass().getClassLoader()
				.getResource("dataset//delete_data.sql").openStream(); EntityManager em = emf.createEntityManager()) {

			BufferedReader sqlFileBufferedReader = new BufferedReader(new InputStreamReader(sqlFileInputStream));
			executeStatements(sqlFileBufferedReader, em);
		} catch (Exception e) {
			e.printStackTrace();
			return HttpStatus.NOT_MODIFIED;
		}
		return HttpStatus.OK;

	}

	void executeStatements(BufferedReader br, EntityManager entityManager) throws IOException {
		String line;
		while ((line = br.readLine()) != null) {
			entityManager.joinTransaction();
			entityManager.createNativeQuery(line).executeUpdate();
		}
	}

	private long toMilliseconds(LocalDateTime date) {
		return date.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
	}

	private void addPhoneNUmber(Person person) {
		person.setPhoneNumbers(Set.of(new PhoneNumber(Source.FISCAL, true, PHONE_NUMBERS, person)));
	}
}
