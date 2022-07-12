INSERT INTO public.address (dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES ('InseeAddress', 'Ted Farmer' ,'','','1 rue de la gare' ,'','29270 Carhaix' ,'France', true, 'Bat. C', 'Etg 4', 'Porte 48', 'Escalier B', true);
INSERT INTO public.address (dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES ('InseeAddress', 'Cecilia Ortega' ,'','','2 place de la mairie' ,'','90000 Belfort' ,'France', false, null, null, null, null, false);
INSERT INTO public.address (dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES ('InseeAddress', 'Claude Watkins' ,'','','3 avenue de la République' ,'','32230 Marciac' ,'France', false, null, null, null, null, false);
INSERT INTO public.address (dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES ('InseeAddress', 'Veronica Gill' ,'','','4 chemin du ruisseau' ,'','44190 Clisson' ,'France', false, null, null, null, null, false);
INSERT INTO public.address (dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES ('InseeAddress', 'Christine Aguilar' ,'','','5 rue de l''école' ,'','59620 Aulnoye-Aimeries' ,'France', false, null, null, null, null, false);
INSERT INTO public.address (dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES ('InseeAddress', 'Louise Walker' ,'','','6 impasse du lac' ,'','38200 Vienne' ,'France', false, null, null, null, null, false);
INSERT INTO public.address (dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES ('InseeAddress', 'Anthony Bennett' ,'','','7 avenue de la Liberté' ,'','62000 Arras' ,'France', false, null, null, null, null, false);
INSERT INTO public.address (dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES ('InseeAddress', 'Christopher Lewis' ,'','','8 route du moulin' ,'','35000 Rennes' ,'France', false, null, null, null, null, false);
INSERT INTO public.address (dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES ('InseeAddress', 'Laurent Neville' ,'','','5 route du sapin' ,'','35000 Rennes' ,'France', false, null, null, null, null, false);

INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-NATIONAL', 'National organizational unit', 'NATIONAL', null);
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-NORTH', 'North region organizational unit', 'LOCAL', 'OU-NATIONAL');
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-SOUTH', 'South region organizational unit', 'LOCAL', 'OU-NATIONAL');
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-WEST', 'West region organizational unit', 'LOCAL', 'OU-NATIONAL');

INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number) VALUES ('INTW1', 'margie.lucas@ou.com', 'Margie', 'Lucas', '+3391231231230');
INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number) VALUES ('INTW2', 'carlton.campbell@ou.com', 'Carlton', 'Campbell', '+3391231231231');
INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number) VALUES ('INTW3', 'gerald.edwards@ou.com', 'Gerald', 'Edwards', '+3391231231231');
INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number) VALUES ('INTW4', 'melody.grant@ou.com', 'Melody', 'Grant', '+3391231231231');

INSERT INTO public.sample_identifier (dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', '11', 11, '1', 11, '11', 11, 11, 11, 11, 11, 1);
INSERT INTO public.sample_identifier (dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', '12', 12, '1', 12, '12', 12, 12, 12, 12, 12, 1);
INSERT INTO public.sample_identifier (dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', '13', 13, '1', 13, '13', 13, 13, 13, 13, 13, 2);
INSERT INTO public.sample_identifier (dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', '14', 14, '1', 14, '14', 14, 14, 14, 14, 14, 3);
INSERT INTO public.sample_identifier (dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', '20', 20, '2', 20, '20', 20, 20, 20, 20, 20, 1);
INSERT INTO public.sample_identifier (dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', '21', 21, '2', 21, '21', 21, 21, 21, 21, 21, 1);
INSERT INTO public.sample_identifier (dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', '22', 22, '2', 22, '22', 22, 22, 22, 22, 22, 2);
INSERT INTO public.sample_identifier (dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', '23', 23, '2', 23, '23', 23, 23, 23, 23, 23, 1);
INSERT INTO public.sample_identifier (dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', '24', 24, '2', 24, '24', 24, 24, 24, 24, 24, 1);

INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('ABC', 'Melinda', 'Webb', 'OU-NORTH');
INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('DEF', 'Everett', 'Juste', 'OU-NORTH');
INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('GHI', 'Elsie', 'Clarke', 'OU-SOUTH');
INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('JKL', 'Julius', 'Howell', 'OU-NATIONAL');
INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('MNO', 'Ted', 'Kannt', 'OU-WEST');

INSERT INTO public.campaign (id, label, email, identification_configuration, contact_attempt_configuration, contact_outcome_configuration) VALUES ('SIMPSONS2020X00', 'Survey on the Simpsons tv show 2020', 'first.email@test.com', 'IASCO', 'F2F', 'F2F');
INSERT INTO public.campaign (id, label, email, identification_configuration, contact_attempt_configuration, contact_outcome_configuration) VALUES ('VQS2021X00', 'Everyday life and health survey 2021', 'second.email@test.com', 'IASCO', 'TEL', 'TEL');
INSERT INTO public.campaign (id, label, email, identification_configuration, contact_attempt_configuration, contact_outcome_configuration) VALUES ('ZCLOSEDX00', 'Everyday life and health survey 2021', 'third.email@test.com', 'IASCO', 'F2F', 'F2F');
INSERT INTO public.campaign (id, label, email, identification_configuration, contact_attempt_configuration, contact_outcome_configuration) VALUES ('XCLOSEDX00', 'Everyday life and health survey 2021', 'fourth.email@test.com', 'IASCO', 'TEL', 'TEL');

INSERT INTO public.preference (id_user, id_campaign) VALUES ('GHI', 'SIMPSONS2020X00');
INSERT INTO public.preference (id_user, id_campaign) VALUES ('JKL', 'SIMPSONS2020X00');
INSERT INTO public.preference (id_user, id_campaign) VALUES ('JKL', 'VQS2021X00');


INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-NORTH', 'SIMPSONS2020X00',  (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '1 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000));
INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-NORTH', 'VQS2021X00',   (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '1 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000));
INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-SOUTH', 'VQS2021X00',  (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '1 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000));
INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-SOUTH', 'SIMPSONS2020X00',  (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '1 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000));
INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-SOUTH', 'ZCLOSEDX00',  (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '1 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '5 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '6 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '7 DAY'))*1000));
INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-WEST', 'ZCLOSEDX00',  (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '1 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '5 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '6 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '7 DAY'))*1000));
INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-SOUTH', 'XCLOSEDX00',  (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '1 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '5 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '6 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '7 DAY'))*1000));

INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '11', TRUE, a.id, 'SIMPSONS2020X00', 'INTW1', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Ted Farmer' AND s.bs='11';
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '12', TRUE, a.id, 'SIMPSONS2020X00', 'INTW1', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Cecilia Ortega' AND s.bs='12';
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '13', FALSE, a.id, 'SIMPSONS2020X00', 'INTW2', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Claude Watkins' AND s.bs='13';
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '14', FALSE, a.id, 'SIMPSONS2020X00', 'INTW3', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Veronica Gill' AND s.bs='14';
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '20', FALSE, a.id, 'VQS2021X00', 'INTW1', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Christine Aguilar' AND s.bs='20';
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '21', FALSE, a.id, 'VQS2021X00', 'INTW2', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Louise Walker' AND s.bs='21';
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '22', FALSE, a.id, 'VQS2021X00', 'INTW4', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Anthony Bennett' AND s.bs='22';
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '23', FALSE, a.id, 'VQS2021X00', 'INTW4', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Christopher Lewis' AND s.bs='23';
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT  '24', TRUE, a.id, 'SIMPSONS2020X00', NULL, s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Laurent Neville' AND s.bs='24';


INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com',TRUE, 'Ted', 'Farmer', 11111111, 0, TRUE, '11');
INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com', TRUE,'Cecilia', 'Ortega', 11111111, 1, TRUE, '12');
INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com', TRUE,'Claude', 'Watkins', 11111111, 0, TRUE, '13');
INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com', TRUE,'Veronica', 'Baker', 11111111, 1, TRUE, '14');
INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com', TRUE,'Christine', 'Aguilar', 11111111, 1, FALSE, '11');
INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com', TRUE,'Louise', 'Walker', 11111111, 1, FALSE, '11');
INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com', TRUE,'Anthony', 'Bennett', 11111111, 0, FALSE, '12');
INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com', TRUE,'Christopher', 'Lewis', 11111111, 0, FALSE, '14');
INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com', TRUE,'Harriette', 'Raymond', 11111111, 0, TRUE, '20');
INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com', TRUE,'Aimée', 'Lamothe', 11111111, 0, TRUE, '21');
INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com', TRUE,'Perrin', 'Blanchard', 11111111, 0, TRUE, '22');
INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com', TRUE,'Artus', 'Arnoux', 11111111, 0, TRUE, '23');
INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES ('test@test.com', TRUE,'Laurent', 'Neville', 11111111, 0, TRUE, '24');



INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Ted' and p.last_name='Farmer' and p.survey_unit_id='11';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT FALSE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Ted' and p.last_name='Farmer' and p.survey_unit_id='11';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Cecilia' and p.last_name='Ortega' and p.survey_unit_id='12';
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



INSERT INTO public.state (date, type, survey_unit_id) VALUES (111112111,'VIN', '11');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (110111111,'NNS', '11');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (111111111,'TBR', '12');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (111111111,'TBR', '13');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (111111111,'TBR', '14');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (101111111,'TBR', '11');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (101111111,'TBR', '24');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'VIC', '20');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'VIC', '21');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'FIN', '22');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'VIC', '23');

INSERT INTO public.contact_outcome (date, type, survey_unit_id) VALUES (1590504478334, 'DUK', '24');

INSERT INTO public.comment (type, value, survey_unit_id) VALUES ('INTERVIEWER', 'un commentaire', '13');

INSERT INTO public.closing_cause (date, type, survey_unit_id) VALUES ((SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAYS'))*1000), 'NPI', '11');


INSERT INTO public.identification (survey_unit_id, identification,access,situation,category,occupant) VALUES ('11', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED');