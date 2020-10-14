INSERT INTO public.campaign (id, end_date, start_date, label) VALUES ('simpsons2020x00', 1640995200000, 1577836800000, 'Survey on the Simpsons tv show 2020');
INSERT INTO public.campaign (id, end_date, start_date, label) VALUES ('vqs2021x00', 1640995200000, 1577836800000, 'Everyday life and health survey 2021');

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

INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504459838, 'NNS', '11');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504468838, 'NNS', '12');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504472342, 'NNS', '13');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'NNS', '14');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'NNS', '20');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'NNS', '21');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'NNS', '22');
INSERT INTO public.state (date, type, survey_unit_id) VALUES (1590504478334, 'NNS', '23');

INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-NORTH', 'simpsons2020x00',  1640995200000, 1577836800000, 1641513600000, 1577232000000, 1576800000000, 1575936000000);
INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-NORTH', 'vqs2021x00',  1640995200000, 1577836800000, 1641513600000, 1577232000000, 1576800000000, 1575936000000);
INSERT INTO public.visibility(organization_unit_id, campaign_id, collection_end_date, collection_start_date, end_date, identification_phase_start_date, interviewer_start_date, management_start_date) VALUES ('OU-SOUTH', 'vqs2021x00',  1640995200000, 1577836800000, 1641513600000, 1577232000000, 1576800000000, 1575936000000);
