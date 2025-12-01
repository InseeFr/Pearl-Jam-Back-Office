--changeset davdarras:reset-data context:test

TRUNCATE TABLE
    public.communication_request_status,
    public.communication_request,
    public.communication_template,
    public.communication_metadata,
    public.contact_attempt,
    public.oumessage_recipient,
    public.referent,
    public.message,
    public.campaign_message_recipient,
    public.message_status,
    public.interviewer,
    public.sample_identifier,
    public.user,
    public.campaign,
    public.preference,
    public.visibility,
    public.contact_history,
    public.survey_unit,
    public.identification,
    public.person,
    public.phone_number,
    public.state,
    public.contact_outcome,
    public.comment,
    public.closing_cause,
    public.organization_unit,
    public.address;

ALTER TABLE public.communication_request_status ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.communication_request ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.communication_metadata ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.contact_attempt ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.referent ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.message ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.sample_identifier ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.identification ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.person ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.phone_number ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.state ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.contact_outcome ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.comment ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.closing_cause ALTER COLUMN id RESTART WITH 1;
ALTER TABLE public.address ALTER COLUMN id RESTART WITH 1;

--changeset davdarras:init-data context:test

INSERT INTO public.address (dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES
    ('InseeAddress', 'Ted Farmer' ,'','','1 rue de la gare' ,'','29270 Carhaix' ,'France', true, 'Bat. C', 'Etg 4', 'Porte 48', 'Escalier B', true),
    ('InseeAddress', 'Cecilia Ortega' ,'','','2 place de la mairie' ,'','90000 Belfort' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Mylene Mikoton' ,'','','3 place de la mairie' ,'','90000 Belfort' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Claude Watkins' ,'','','3 avenue de la République' ,'','32230 Marciac' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Veronica Gill' ,'','','4 chemin du ruisseau' ,'','44190 Clisson' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Christine Aguilar' ,'','','5 rue de l''école' ,'','59620 Aulnoye-Aimeries' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Louise Walker' ,'','','6 impasse du lac' ,'','38200 Vienne' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Anthony Bennett' ,'','','7 avenue de la Liberté' ,'','62000 Arras' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Christopher Lewis' ,'','','8 route du moulin' ,'','35000 Rennes' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Laurent Neville' ,'','','5 route du sapin' ,'','35000 Rennes' ,'France', false, null, null, null, null, false);

INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-NATIONAL', 'National organizational unit', 'NATIONAL', null);
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-NORTH', 'North region organizational unit', 'LOCAL', 'OU-NATIONAL');
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-SOUTH', 'South region organizational unit', 'LOCAL', 'OU-NATIONAL');
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-WEST', 'West region organizational unit', 'LOCAL', 'OU-NATIONAL');

INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number) VALUES
    ('INTW1', 'margie.lucas@ou.com', 'Margie', 'Lucas', '+3391231231230'),
    ('INTW2', 'carlton.campbell@ou.com', 'Carlton', 'Campbell', '+3391231231231'),
    ('INTW3', 'gerald.edwards@ou.com', 'Gerald', 'Edwards', '+3391231231231'),
    ('INTW4', 'melody.grant@ou.com', 'Melody', 'Grant', '+3391231231231');

INSERT INTO public.sample_identifier (dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES
    ('InseeSampleIdentifier', '11', 11, '1', 11, '11', 11, 11, 11, 11, 11, 1),
    ('InseeSampleIdentifier', '12', 12, '1', 12, '12', 12, 12, 12, 12, 12, 1),
    ('InseeSampleIdentifier', '13', 13, '1', 13, '13', 13, 13, 13, 13, 13, 2),
    ('InseeSampleIdentifier', '14', 14, '1', 14, '14', 14, 14, 14, 14, 14, 3),
    ('InseeSampleIdentifier', '20', 20, '2', 20, '20', 20, 20, 20, 20, 20, 1),
    ('InseeSampleIdentifier', '21', 21, '2', 21, '21', 21, 21, 21, 21, 21, 1),
    ('InseeSampleIdentifier', '22', 22, '2', 22, '22', 22, 22, 22, 22, 22, 2),
    ('InseeSampleIdentifier', '23', 23, '2', 23, '23', 23, 23, 23, 23, 23, 1),
    ('InseeSampleIdentifier', '24', 24, '2', 24, '24', 24, 24, 24, 24, 24, 1),
    ('InseeSampleIdentifier', '25', 25, '2', 25, '25', 25, 25, 25, 25, 25, 1);

INSERT INTO public.USER (id, first_name, last_name, organization_unit_id) VALUES
    ('ABC', 'Melinda', 'Webb', 'OU-NORTH'),
    ('DEF', 'Everett', 'Juste', 'OU-NORTH'),
    ('GHI', 'Elsie', 'Clarke', 'OU-SOUTH'),
    ('JKL', 'Julius', 'Howell', 'OU-NATIONAL'),
    ('MNO', 'Ted', 'Kannt', 'OU-WEST'),
    ('GUEST', 'firstname', 'lastname', 'OU-NATIONAL');

INSERT INTO public.campaign (id, label, email, identification_configuration, contact_attempt_configuration, contact_outcome_configuration) VALUES
    ('SIMPSONS2020X00', 'Survey on the Simpsons tv show 2020', 'first.email@test.com', 'IASCO', 'F2F', 'F2F'),
    ('VQS2021X00', 'Everyday life and health survey 2021', 'second.email@test.com', 'IASCO', 'TEL', 'TEL'),
    ('ZCLOSEDX00', 'Everyday life and health survey 2021', 'third.email@test.com', 'IASCO', 'F2F', 'F2F'),
    ('XCLOSEDX00', 'Everyday life and health survey 2021', 'fourth.email@test.com', 'IASCO', 'TEL', 'TEL');

INSERT INTO public.preference (id_user, id_campaign) VALUES
    ('GHI', 'SIMPSONS2020X00'),
    ('JKL', 'SIMPSONS2020X00'),
    ('JKL', 'VQS2021X00'),
    ('GUEST', 'SIMPSONS2020X00'),
    ('GUEST', 'VQS2021X00'),
    ('GUEST', 'ZCLOSEDX00'),
    ('GUEST', 'XCLOSEDX00');

INSERT INTO visibility (
    organization_unit_id,
    campaign_id,
    collection_end_date,
    collection_start_date,
    end_date,
    identification_phase_start_date,
    interviewer_start_date,
    management_start_date,
    use_letter_communication,
    mail,
    tel
) VALUES
    ('OU-NORTH', 'SIMPSONS2020X00',1721903754305, 1719225354304, 1724582154306, 1719138954303, 1719052554302, 1718966154301, true, 'north-simpsons@nooneknows.fr', '0321234567'),
    ('OU-SOUTH', 'SIMPSONS2020X00',1721903754315, 1719225354314, 1724582154316, 1719138954310, 1719052554309, 1718966154308, false, 'south-simpsons@nooneknows.fr', ''),
    ('OU-NORTH', 'VQS2021X00',1721903754308, 1719225354308, 1724582154308, 1719138954308, 1719052554308, 1718966154308, false, 'north-vqs@nooneknows.fr', ''),
    ('OU-SOUTH', 'VQS2021X00',1721903754308, 1719225354308, 1724582154308, 1719138954308, 1719052554308, 1718966154308, true, 'north-vqs@nooneknows.fr', ''),
    ('OU-SOUTH', 'XCLOSEDX00',1719052554308, 1718966154308, 1719225354308, 1718879754308, 1718789354308, 1718706954308, true, 'north-vqs@nooneknows.fr', ''),
    ('OU-SOUTH', 'ZCLOSEDX00',1719052554308, 1718966154308, 1719225354308, 1718879754308, 1718789354308, 1718706954308, true, 'north-vqs@nooneknows.fr', ''),
    ('OU-WEST' , 'ZCLOSEDX00',1719052554308, 1718966154308, 1719225354308, 1718879754308, 1718789354308, 1718706954308, true, 'north-vqs@nooneknows.fr', '');

INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '11', 'business-id-11',TRUE, a.id, 'SIMPSONS2020X00', 'INTW1', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Ted Farmer' AND s.bs='11';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '12', 'business-id-12', TRUE, a.id, 'SIMPSONS2020X00', 'INTW1', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Cecilia Ortega' AND s.bs='12';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '25', 'business-id-25', TRUE, a.id, 'SIMPSONS2020X00', 'INTW1', s.id, 'OU-SOUTH' FROM address a, sample_identifier s WHERE a.l1='Mylene Mikoton' AND s.bs='25';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '13', 'business-id-13', FALSE, a.id, 'SIMPSONS2020X00', 'INTW2', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Claude Watkins' AND s.bs='13';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '14', 'business-id-14', FALSE, a.id, 'SIMPSONS2020X00', 'INTW3', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Veronica Gill' AND s.bs='14';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '20', 'business-id-20', FALSE, a.id, 'VQS2021X00', 'INTW1', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Christine Aguilar' AND s.bs='20';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '21', 'business-id-21', FALSE, a.id, 'VQS2021X00', 'INTW2', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Louise Walker' AND s.bs='21';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '22', 'business-id-22', FALSE, a.id, 'VQS2021X00', 'INTW4', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Anthony Bennett' AND s.bs='22';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '23', 'business-id-23', FALSE, a.id, 'VQS2021X00', 'INTW4', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Christopher Lewis' AND s.bs='23';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '24', 'business-id-24', TRUE, a.id, 'SIMPSONS2020X00', NULL, s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Laurent Neville' AND s.bs='24';


INSERT INTO public.person (email, first_name, last_name, birthdate, title, privileged, survey_unit_id, panel, contact_history_type) VALUES
    ('test@test.com', 'Ted', 'Farmer', 11111111, 0, TRUE, '11', FALSE, NULL),
    ('test@test.com', 'Cecilia', 'Ortega', 11111111, 1, TRUE, '12', FALSE, NULL),
    ('test@test.com', 'Mylene', 'Mikoton', 11111111, 1, TRUE, '25', FALSE, NULL),
    ('test@test.com', 'Claude', 'Watkins', 11111111, 0, TRUE, '13', FALSE, NULL),
    ('test@test.com', 'Veronica', 'Baker', 11111111, 1, TRUE, '14', FALSE, NULL),
    ('test@test.com', 'Christine', 'Aguilar', 11111111, 1, FALSE, '11', FALSE, NULL),
    ('test@test.com', 'Louise', 'Walker', 11111111, 1, FALSE, '11', FALSE, NULL),
    ('test@test.com', 'Anthony', 'Bennett', 11111111, 0, FALSE, '12', FALSE, NULL),
    ('test@test.com', 'Christopher', 'Lewis', 11111111, 0, FALSE, '14', FALSE, NULL),
    ('test@test.com', 'Harriette', 'Raymond', 11111111, 0, TRUE, '20', FALSE, NULL),
    ('test@test.com', 'Aimée', 'Lamothe', 11111111, 0, TRUE, '21', FALSE, NULL),
    ('test@test.com', 'Perrin', 'Blanchard', 11111111, 0, TRUE, '22', FALSE, NULL),
    ('test@test.com', 'Artus', 'Arnoux', 11111111, 0, TRUE, '23', FALSE, NULL),
    ('test@test.com', 'Laurent', 'Neville', 11111111, 0, TRUE, '24', FALSE, NULL),
    (null           , 'Pedro',    null,         null, null, FALSE, '11', null, 'PREVIOUS'),
    ('futur.ama@ch.com', 'Futur', 'Ama', null, 1, TRUE, '11', FALSE, 'NEXT'),
    ('test@ch.com', 'Opre', 'Vious', 315532800000, 1, TRUE, '11', TRUE, 'PREVIOUS'),
    ('test@ch.com', 'Agrippa', 'Nel', 1024815788000, 0, FALSE, '11', TRUE, 'PREVIOUS'),
    ('test@ch.com', 'Isidore', 'Champ', 1070870588000, 0, FALSE, '11', FALSE, 'PREVIOUS');

INSERT INTO public.contact_history (survey_unit_id, contact_history_type, contact_outcome_value, comment) VALUES
    ('11', 'NEXT', NULL, NULL),
    ('11', 'PREVIOUS', 'INA', 'nice comment'),
    ('20', 'NEXT', NULL, NULL);


INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Ted' and p.last_name='Farmer' and p.survey_unit_id='11';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT FALSE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Ted' and p.last_name='Farmer' and p.survey_unit_id='11';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Cecilia' and p.last_name='Ortega' and p.survey_unit_id='12';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Mylene' and p.last_name='Mikoton' and p.survey_unit_id='25';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Claude' and p.last_name='Watkins' and p.survey_unit_id='13';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Veronica' and p.last_name='Baker' and p.survey_unit_id='14';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Christine' and p.last_name='Aguilar' and p.survey_unit_id='11';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Louise' and p.last_name='Walker' and p.survey_unit_id='11';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Anthony' and p.last_name='Bennett' and p.survey_unit_id='12';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Christopher' and p.last_name='Lewis' and p.survey_unit_id='14';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Harriette' and p.last_name='Raymond' and p.survey_unit_id='20';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Aimée' and p.last_name='Lamothe' and p.survey_unit_id='21';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Perrin' and p.last_name='Blanchard' and p.survey_unit_id='22';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Artus' and p.last_name='Arnoux' and p.survey_unit_id='23';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Laurent' and p.last_name='Neville' and p.survey_unit_id='24';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542866', 2,  p.id FROM person p WHERE p.first_name='Futur' and p.last_name='Ama' and p.survey_unit_id='11';

INSERT INTO public.state (date, type, survey_unit_id) VALUES
    (111112111,'VIN', '11'),
    (110111111,'NNS', '11'),
    (111111111,'TBR', '12'),
    (111111111,'TBR', '25'),
    (111111111,'TBR', '13'),
    (111111111,'TBR', '14'),
    (101111111,'TBR', '11'),
    (101111111,'TBR', '24'),
    (1590504478334, 'VIC', '20'),
    (1590504478334, 'VIC', '21'),
    (1590504478334, 'FIN', '22'),
    (1590504478334, 'VIC', '23');

INSERT INTO public.contact_outcome (date, type, survey_unit_id) VALUES
    (1590504478334, 'DUK', '24');

INSERT INTO public.referent (campaign_id, first_name, last_name, role, phone_number) VALUES
    ('SIMPSONS2020X00', 'Gerard', 'Menvuca', 'PRIMARY', '0303030303');

INSERT INTO public.comment (type, value, survey_unit_id) VALUES
    ('INTERVIEWER', 'un commentaire', '13');

INSERT INTO closing_cause ("date", type, survey_unit_id)
VALUES (
  EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - INTERVAL '3 day')) * 1000,
  'NPI',
  '11'
);


INSERT INTO public.identification (survey_unit_id, identification_type, identification,access,situation,category,occupant,individual_status,interviewer_can_process,number_of_respondents,present_in_previous_home,household_composition,identification_state) VALUES
    ('11', 'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED','SAME_ADDRESS','YES','MANY','AT_LEAST_ONE','SAME_COMPO','FINISHED'),
    ('21', 'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED', null, null, null, null, null,'FINISHED');

INSERT INTO public.communication_template (meshuggah_id, medium, type, campaign_id) VALUES
    ('mesh1', 'EMAIL', 'REMINDER', 'SIMPSONS2020X00'),
    ('mesh2', 'LETTER', 'NOTICE', 'SIMPSONS2020X00'),
    ('mesh3', 'EMAIL', 'REMINDER', 'VQS2021X00'),
    ('mesh4', 'LETTER', 'NOTICE', 'VQS2021X00'),
    ('mesh5', 'EMAIL', 'NOTICE', 'VQS2021X00');

INSERT INTO public.communication_request (survey_unit_id, emitter, reason, campaign_id, meshuggah_id) VALUES
    ('11', 'INTERVIEWER', 'REFUSAL', 'SIMPSONS2020X00', 'mesh1'),
    ('11', 'INTERVIEWER', 'UNREACHABLE', 'SIMPSONS2020X00', 'mesh2'),
    ('20', 'INTERVIEWER', 'REFUSAL', 'VQS2021X00', 'mesh3'),
    ('20', 'INTERVIEWER', 'UNREACHABLE', 'VQS2021X00', 'mesh4');

INSERT INTO public.communication_request_status (communication_request_id, status, date) VALUES
    (1, 'INITIATED', 1721903754305),
    (1, 'READY', 1721903755305),
    (1, 'SUBMITTED', 1721903756305),
    (2, 'INITIATED', 1721903754305),
    (2, 'READY', 1721903756310),
    (3, 'INITIATED', 1721903754205),
    (4, 'INITIATED', 1721903754205);

INSERT INTO public.communication_metadata (survey_unit_id, campaign_id, meshuggah_id, metadata_key, metadata_value) VALUES
    ('11', 'SIMPSONS2020X00','mesh1','recipient_full_name', 'Albert Einstein'),
    ('11', 'SIMPSONS2020X00','mesh1','recipient_address', '112 Mercer Street, Princeton, New Jersey');
