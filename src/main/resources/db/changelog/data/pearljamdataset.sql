INSERT INTO public.address (dtype, id, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES ('InseeAddress', 1, 'Ted Farmer' ,'','','1 rue de la gare' ,'','29270 Carhaix' ,'France', '29024');
INSERT INTO public.address (dtype, id, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES ('InseeAddress', 2, 'Cecilia Ortega' ,'','','2 place de la mairie' ,'','90000 Belfort' ,'France', '90010');
INSERT INTO public.address (dtype, id, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES ('InseeAddress', 3, 'Claude Watkins' ,'','','3 avenue de la République' ,'','32230 Marciac' ,'France', '32233');
INSERT INTO public.address (dtype, id, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES ('InseeAddress', 4, 'Veronica Gill' ,'','','4 chemin du ruisseau' ,'','44190 Clisson' ,'France', '44043');
INSERT INTO public.address (dtype, id, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES ('InseeAddress', 5, 'Christine Aguilar' ,'','','5 rue de l''école' ,'','59620 Aulnoye-Aimeries' ,'France', '59033');
INSERT INTO public.address (dtype, id, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES ('InseeAddress', 6, 'Louise Walker' ,'','','6 impasse du lac' ,'','38200 Vienne' ,'France', '38544');
INSERT INTO public.address (dtype, id, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES ('InseeAddress', 7, 'Anthony Bennett' ,'','','7 avenue de la Liberté' ,'','62000 Arras' ,'France', '62041');
INSERT INTO public.address (dtype, id, l1, l2, l3, l4, l5, l6, l7, geographical_location_id) VALUES ('InseeAddress', 8, 'Christopher Lewis' ,'','','8 route du moulin' ,'','35000 Rennes' ,'France', '35238');

INSERT INTO public.campaign (id, collection_end_date, collection_start_date, label) VALUES ('simpsons2020x00', 1640995200, 1577836800, 'Survey on the Simpsons tv show 2020');
INSERT INTO public.campaign (id, collection_end_date, collection_start_date, label) VALUES ('vqs2021x00', 1640995200, 1577836800, 'Everyday life and health survey 2021');

INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-NATIONAL', 'National organizational unit', 'NATIONAL', null);
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-NORTH', 'North region organizational unit', 'LOCAL', 'OU-NATIONAL');
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-SOUTH', 'South region organizational unit', 'LOCAL', 'OU-NATIONAL');

INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number, organization_unit_id) VALUES ('INTW1', 'margie.lucas@ou.com', 'Margie', 'Lucas', '+3391231231230', 'OU-NORTH');
INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number, organization_unit_id) VALUES ('INTW2', 'carlton.campbell@ou.com', 'Carlton', 'Campbell', '+3391231231231', 'OU-NORTH');
INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number, organization_unit_id) VALUES ('INTW3', 'gerald.edwards@ou.com', 'Gerald', 'Edwards', '+3391231231231', 'OU-NORTH');
INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number, organization_unit_id) VALUES ('INTW4', 'melody.grant@ou.com', 'Melody', 'Grant', '+3391231231231', 'OU-SOUTH');

INSERT INTO public.sample_identifier (dtype, id, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', 1, '11', 11, '1', 11, '11', 11, 11, 11, 11, 11, 1);
INSERT INTO public.sample_identifier (dtype, id, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', 2, '12', 12, '1', 12, '12', 12, 12, 12, 12, 12, 1);
INSERT INTO public.sample_identifier (dtype, id, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', 3, '13', 13, '1', 13, '13', 13, 13, 13, 13, 13, 2);
INSERT INTO public.sample_identifier (dtype, id, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', 4, '14', 14, '1', 14, '14', 14, 14, 14, 14, 14, 3);
INSERT INTO public.sample_identifier (dtype, id, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', 5, '20', 20, '2', 20, '20', 20, 20, 20, 20, 20, 1);
INSERT INTO public.sample_identifier (dtype, id, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', 6, '21', 21, '2', 21, '21', 21, 21, 21, 21, 21, 1);
INSERT INTO public.sample_identifier (dtype, id, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', 7, '22', 22, '2', 22, '22', 22, 22, 22, 22, 22, 2);
INSERT INTO public.sample_identifier (dtype, id, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES ('InseeSampleIdentifier', 8, '23', 23, '2', 23, '23', 23, 23, 23, 23, 23, 1);

INSERT INTO public.survey_unit (id, first_name, last_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id) VALUES ('11', 'Ted', 'Farmer', TRUE, 1, 'simpsons2020x00', 'INTW1', 1);
INSERT INTO public.survey_unit (id, first_name, last_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id) VALUES ('12', 'Cecilia', 'Ortega', TRUE, 2, 'simpsons2020x00', 'INTW1', 2);
INSERT INTO public.survey_unit (id, first_name, last_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id) VALUES ('13', 'Claude', 'Watkins', FALSE, 3, 'simpsons2020x00', 'INTW2', 3);
INSERT INTO public.survey_unit (id, first_name, last_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id) VALUES ('14', 'Veronica', 'Gill', FALSE, 4, 'simpsons2020x00', 'INTW3', 4);
INSERT INTO public.survey_unit (id, first_name, last_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id) VALUES ('20', 'Christine', 'Aguilar', TRUE, 5, 'vqs2021x00', 'INTW1', 5);
INSERT INTO public.survey_unit (id, first_name, last_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id) VALUES ('21', 'Louise', 'Walker', TRUE, 6, 'vqs2021x00', 'INTW2', 6);
INSERT INTO public.survey_unit (id, first_name, last_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id) VALUES ('22', 'Anthony', 'Bennett', FALSE, 7, 'vqs2021x00', 'INTW4', 7);
INSERT INTO public.survey_unit (id, first_name, last_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id) VALUES ('23', 'Christopher', 'Lewis', TRUE, 8, 'vqs2021x00', 'INTW4', 8);

INSERT INTO public.survey_unit_phone_numbers(survey_unit_id, phone_numbers) VALUES ('11', '+3351231231230');
INSERT INTO public.survey_unit_phone_numbers(survey_unit_id, phone_numbers) VALUES ('12', '+3351231231231');
INSERT INTO public.survey_unit_phone_numbers(survey_unit_id, phone_numbers) VALUES ('13', '+3351231231232');
INSERT INTO public.survey_unit_phone_numbers(survey_unit_id, phone_numbers) VALUES ('14', '+3351231231233');
INSERT INTO public.survey_unit_phone_numbers(survey_unit_id, phone_numbers) VALUES ('20', '+3351231231234');
INSERT INTO public.survey_unit_phone_numbers(survey_unit_id, phone_numbers) VALUES ('21', '+3351231231235');
INSERT INTO public.survey_unit_phone_numbers(survey_unit_id, phone_numbers) VALUES ('22', '+3351231231236');
INSERT INTO public.survey_unit_phone_numbers(survey_unit_id, phone_numbers) VALUES ('23', '+3351231231237');

INSERT INTO public.state (id, date, type, survey_unit_id) VALUES (nextval('public.state_seq'), 1588236041, 'ANS', '11');
INSERT INTO public.state (id, date, type, survey_unit_id) VALUES (nextval('public.state_seq'), 1588149641, 'ANS', '12');
INSERT INTO public.state (id, date, type, survey_unit_id) VALUES (nextval('public.state_seq'), 1588063241, 'ANS', '13');
INSERT INTO public.state (id, date, type, survey_unit_id) VALUES (nextval('public.state_seq'), 1587976841, 'ANS', '14');
INSERT INTO public.state (id, date, type, survey_unit_id) VALUES (nextval('public.state_seq'), 1587890441, 'ANS', '20');
INSERT INTO public.state (id, date, type, survey_unit_id) VALUES (nextval('public.state_seq'), 1587890441, 'ANS', '21');
INSERT INTO public.state (id, date, type, survey_unit_id) VALUES (nextval('public.state_seq'), 1587890441, 'ANS', '22');
INSERT INTO public.state (id, date, type, survey_unit_id) VALUES (nextval('public.state_seq'), 1587890441, 'ANS', '23');

INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('ABC', 'Melinda', 'Webb', 'OU-NORTH');
INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('DEF', 'Everett', 'Juste', 'OU-NORTH');
INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('GHI', 'Elsie', 'Clarke', 'OU-SOUTH');
INSERT INTO public."user" (id, first_name, last_name, organization_unit_id) VALUES ('JKL', 'Julius', 'Howell', 'OU-NATIONAL');

INSERT INTO public.preference (id_user, id_campaign) VALUES ('ABC', 'simpsons2020x00');
INSERT INTO public.preference (id_user, id_campaign) VALUES ('ABC', 'vqs2021x00');
INSERT INTO public.preference (id_user, id_campaign) VALUES ('DEF', 'simpsons2020x00');
INSERT INTO public.preference (id_user, id_campaign) VALUES ('DEF', 'vqs2021x00');
INSERT INTO public.preference (id_user, id_campaign) VALUES ('GHI', 'vqs2021x00');

INSERT INTO public.visibility (collection_end_date, collection_start_date, organization_unit_id, campaign_id) VALUES (1640908800, 1577923200, 'OU-NORTH', 'simpsons2020x00');
INSERT INTO public.visibility (collection_end_date, collection_start_date, organization_unit_id, campaign_id) VALUES (1640908800, 1577923200, 'OU-NORTH', 'vqs2021x00');
INSERT INTO public.visibility (collection_end_date, collection_start_date, organization_unit_id, campaign_id) VALUES (1640908800, 1577923200, 'OU-SOUTH', 'vqs2021x00');

