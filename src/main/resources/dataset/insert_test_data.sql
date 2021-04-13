INSERT INTO public.address (id, dtype, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES (1,'InseeAddress', 'Ted Farmer' ,'','','1 rue de la gare' ,'','29270 Carhaix' ,'France', '29024');
INSERT INTO public.address (id, dtype, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES (2,'InseeAddress', 'Cecilia Ortega' ,'','','2 place de la mairie' ,'','90000 Belfort' ,'France', '90010');
INSERT INTO public.address (id, dtype, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES (3,'InseeAddress', 'Claude Watkins' ,'','','3 avenue de la République' ,'','32230 Marciac' ,'France', '32233');
INSERT INTO public.address (id, dtype, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES (4,'InseeAddress', 'Veronica Gill' ,'','','4 chemin du ruisseau' ,'','44190 Clisson' ,'France', '44043');
INSERT INTO public.address (id, dtype, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES (5,'InseeAddress', 'Christine Aguilar' ,'','','5 rue de l''école' ,'','59620 Aulnoye-Aimeries' ,'France', '59033');
INSERT INTO public.address (id, dtype, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES (6,'InseeAddress', 'Louise Walker' ,'','','6 impasse du lac' ,'','38200 Vienne' ,'France', '38544');
INSERT INTO public.address (id, dtype, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES (7,'InseeAddress', 'Anthony Bennett' ,'','','7 avenue de la Liberté' ,'','62000 Arras' ,'France', '62041');
INSERT INTO public.address (id, dtype, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES (8,'InseeAddress', 'Christopher Lewis' ,'','','8 route du moulin' ,'','35000 Rennes' ,'France', '35238');

INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-NATIONAL', 'National organizational unit', 'NATIONAL', null);
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-NORTH', 'North region organizational unit', 'LOCAL', 'OU-NATIONAL');
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-SOUTH', 'South region organizational unit', 'LOCAL', 'OU-NATIONAL');

INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number) VALUES ('INTW1', 'margie.lucas@ou.com', 'Margie', 'Lucas', '+3391231231230');
INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number) VALUES ('INTW2', 'carlton.campbell@ou.com', 'Carlton', 'Campbell', '+3391231231231');
INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number) VALUES ('INTW3', 'gerald.edwards@ou.com', 'Gerald', 'Edwards', '+3391231231231');
INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number) VALUES ('INTW4', 'melody.grant@ou.com', 'Melody', 'Grant', '+3391231231231');

INSERT INTO public.sample_identifier (id, dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES (1,'InseeSampleIdentifier', '11', 11, '1', 11, '11', 11, 11, 11, 11, 11, 1);
INSERT INTO public.sample_identifier (id, dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES (2,'InseeSampleIdentifier', '12', 12, '1', 12, '12', 12, 12, 12, 12, 12, 1);
INSERT INTO public.sample_identifier (id, dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES (3,'InseeSampleIdentifier', '13', 13, '1', 13, '13', 13, 13, 13, 13, 13, 2);
INSERT INTO public.sample_identifier (id, dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES (4,'InseeSampleIdentifier', '14', 14, '1', 14, '14', 14, 14, 14, 14, 14, 3);
INSERT INTO public.sample_identifier (id, dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES (5,'InseeSampleIdentifier', '20', 20, '2', 20, '20', 20, 20, 20, 20, 20, 1);
INSERT INTO public.sample_identifier (id, dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES (6,'InseeSampleIdentifier', '21', 21, '2', 21, '21', 21, 21, 21, 21, 21, 1);
INSERT INTO public.sample_identifier (id, dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES (7,'InseeSampleIdentifier', '22', 22, '2', 22, '22', 22, 22, 22, 22, 22, 2);
INSERT INTO public.sample_identifier (id, dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES (8,'InseeSampleIdentifier', '23', 23, '2', 23, '23', 23, 23, 23, 23, 23, 1);

INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('ABC', 'Melinda', 'Webb', 'OU-NORTH');
INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('DEF', 'Everett', 'Juste', 'OU-NORTH');
INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('GHI', 'Elsie', 'Clarke', 'OU-SOUTH');
INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('JKL', 'Julius', 'Howell', 'OU-NATIONAL');

INSERT INTO public.campaign (id, end_date, start_date, label) VALUES ('simpsons2020x00', (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '5 DAY'))*1000), 'Survey on the Simpsons tv show 2020');
INSERT INTO public.campaign (id, end_date, start_date, label) VALUES ('vqs2021x00', (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '5 DAY'))*1000), 'Everyday life and health survey 2021');

INSERT INTO public.preference (id_user, id_campaign) VALUES ('ABC', 'simpsons2020x00');
INSERT INTO public.preference (id_user, id_campaign) VALUES ('ABC', 'vqs2021x00');
INSERT INTO public.preference (id_user, id_campaign) VALUES ('DEF', 'simpsons2020x00');
INSERT INTO public.preference (id_user, id_campaign) VALUES ('DEF', 'vqs2021x00');
INSERT INTO public.preference (id_user, id_campaign) VALUES ('GHI', 'vqs2021x00');

INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-NORTH', 'simpsons2020x00',  (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '1 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000));
INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-NORTH', 'vqs2021x00',   (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '1 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000));
INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-SOUTH', 'vqs2021x00',  (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '1 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000));
INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-SOUTH', 'simpsons2020x00',  (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '1 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000));

-- INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-NORTH', 'simpsons2020x00',  (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '1 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000));
-- INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-NORTH', 'vqs2021x00',   (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '1 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000));
-- INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-SOUTH', 'vqs2021x00',  (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '1 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000));
-- INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-SOUTH', 'simpsons2020x00',  (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '1 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP + INTERVAL '2 MONTH'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '2 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAY'))*1000), (SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '4 DAY'))*1000));


INSERT INTO public.survey_unit (id,priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) VALUES ('11', TRUE, 1, 'simpsons2020x00', 'INTW1', 1, 'OU-NORTH');
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) VALUES ('12', TRUE, 2, 'simpsons2020x00', 'INTW1', 2, 'OU-NORTH');
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) VALUES ('13', FALSE, 3, 'simpsons2020x00', 'INTW2', 3, 'OU-NORTH');
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) VALUES ('14', FALSE, 4, 'simpsons2020x00', 'INTW3', 4, 'OU-NORTH');
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) VALUES ('20', TRUE, 5, 'vqs2021x00', 'INTW1', 5, 'OU-NORTH');
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) VALUES ('21', TRUE, 6, 'vqs2021x00', 'INTW2', 6, 'OU-NORTH');
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) VALUES ('22', FALSE, 7, 'vqs2021x00', 'INTW4', 7, 'OU-NORTH');
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) VALUES ('23', TRUE, 8, 'vqs2021x00', 'INTW4', 8, 'OU-NORTH');
INSERT INTO public.survey_unit (id, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) VALUES ('24', TRUE, 8, 'simpsons2020x00', NULL, 8, 'OU-NORTH');


INSERT INTO public.person (id, email, favorite_email, first_name, last_name, birthdate, title, survey_unit_id) VALUES (1, 'test@test.com',TRUE, 'Ted', 'Farmer', 11111111, 0, 11);
INSERT INTO public.person (id, email, favorite_email, first_name, last_name, birthdate, title, survey_unit_id) VALUES (2, 'test@test.com', TRUE,'Cecilia', 'Ortega', 11111111, 1,  12);
INSERT INTO public.person (id, email, favorite_email, first_name, last_name, birthdate, title, survey_unit_id) VALUES (3, 'test@test.com', TRUE,'Claude', 'Watkins', 11111111, 0,  13);
INSERT INTO public.person (id, email, favorite_email, first_name, last_name, birthdate, title, survey_unit_id) VALUES (4,'test@test.com', TRUE,'Veronica', 'Baker', 11111111, 1,  14);
INSERT INTO public.person (id, email, favorite_email, first_name, last_name, birthdate, title, survey_unit_id) VALUES (5,'test@test.com', TRUE,'Christine', 'Aguilar', 11111111, 1,  11);
INSERT INTO public.person (id, email, favorite_email, first_name, last_name, birthdate, title, survey_unit_id) VALUES (6,'test@test.com', TRUE,'Louise', 'Walker', 11111111, 1,  11);
INSERT INTO public.person (id, email, favorite_email, first_name, last_name, birthdate, title, survey_unit_id) VALUES (7, 'test@test.com', TRUE,'Anthony', 'Bennett', 11111111, 0,  12);
INSERT INTO public.person (id, email, favorite_email, first_name, last_name, birthdate, title, survey_unit_id) VALUES (8, 'test@test.com', TRUE,'Christopher', 'Lewis', 11111111, 0,  14);
INSERT INTO public.person (id, email, favorite_email, first_name, last_name, birthdate, title, survey_unit_id) VALUES (9, 'test@test.com', TRUE,'Claude', 'Watkins', 11111111, 0,  20);



INSERT INTO public.phone_number (favorite, number, source, person_id) VALUES (TRUE,'+33677542802', 0,  1);
INSERT INTO public.phone_number (favorite, number, source, person_id) VALUES (FALSE,'+33677542802', 0,  1);
INSERT INTO public.phone_number (favorite, number, source, person_id) VALUES (TRUE,'+33677542802', 0,  2);
INSERT INTO public.phone_number (favorite, number, source, person_id) VALUES (TRUE,'+33677542802', 0,  3);
INSERT INTO public.phone_number (favorite, number, source, person_id) VALUES (TRUE,'+33677542802', 0,  4);
INSERT INTO public.phone_number (favorite, number, source, person_id) VALUES (TRUE,'+33677542802', 0,  5);
INSERT INTO public.phone_number (favorite, number, source, person_id) VALUES (TRUE,'+33677542802', 0,  6);
INSERT INTO public.phone_number (favorite, number, source, person_id) VALUES (TRUE,'+33677542802', 0,  7);
INSERT INTO public.phone_number (favorite, number, source, person_id) VALUES (TRUE,'+33677542802', 0,  8);
INSERT INTO public.phone_number (favorite, number, source, person_id) VALUES (TRUE,'+33677542802', 0,  9);

INSERT INTO public.state (date, type, survey_unit_id) VALUES (111112111,'VIN', 11);
INSERT INTO public.state (date, type, survey_unit_id) VALUES (110111111,'NNS', 11);
INSERT INTO public.state (date, type, survey_unit_id) VALUES (111111111,'TBR', 12);
INSERT INTO public.state (date, type, survey_unit_id) VALUES (111111111,'TBR', 13);
INSERT INTO public.state (date, type, survey_unit_id) VALUES (111111111,'TBR', 14);
INSERT INTO public.state (date, type, survey_unit_id) VALUES (101111111,'TBR', 11);
INSERT INTO public.state (date, type, survey_unit_id) VALUES (101111111,'TBR', 24);
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'VIC', '20');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'VIC', '21');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'FIN', '22');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'VIC', '23');

INSERT INTO public.contact_outcome (date, type, survey_unit_id) VALUES (1590504478334, 'WAM', '24');



INSERT INTO public.comment (type, value, survey_unit_id) VALUES ('INTERVIEWER', 'un commentaire',13);

INSERT INTO public.closing_cause (date, type, survey_unit_id) VALUES ((SELECT extract(epoch from (LOCALTIMESTAMP - INTERVAL '3 DAYS'))*1000), 'NPI', '11');


