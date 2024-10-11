--changeset davdarras:init-demo-data

INSERT INTO public.address (dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES
    ('InseeAddress', 'Ted Farmer' ,'','','1 rue de la gare' ,'','29270 Carhaix' ,'France', true, 'Bat. C', 'Etg 4', 'Porte 48', 'Escalier B', true),
    ('InseeAddress', 'Cecilia Ortega' ,'','','2 place de la mairie' ,'','90000 Belfort' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Claude Watkins' ,'','','3 avenue de la République' ,'','32230 Marciac' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Veronica Gill' ,'','','4 chemin du ruisseau' ,'','44190 Clisson' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Christine Aguilar' ,'','','5 rue de l''école' ,'','59620 Aulnoye-Aimeries' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Louise Walker' ,'','','6 impasse du lac' ,'','38200 Vienne' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Anthony Bennett' ,'','','7 avenue de la Liberté' ,'','62000 Arras' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Christopher Lewis' ,'','','8 route du moulin' ,'','35000 Rennes' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Laurent Neville' ,'','','5 route du sapin' ,'','35000 Rennes' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Alain Thé' ,'','','7 rue des Infusions' ,'','75001 Paris' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Brie Savarin' ,'','','15 avenue des Fromages' ,'','69002 Lyon' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Cécile Houte' ,'','','8 impasse des Aromates' ,'','13003 Marseille' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Dan Tifrice' ,'','','12 chemin du Dentifrice' ,'','33000 Bordeaux' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Émile Pates' ,'','','3 place des Pâtes' ,'','59000 Lille' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'François Appétit' ,'','','22 rue de la Faim' ,'','67000 Strasbourg' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Gérard Dine' ,'','','9 allée des Gourmands' ,'','44000 Nantes' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Hélène Gume' ,'','','18 boulevard des Légumes' ,'','31000 Toulouse' ,'France', false, null, null, null, null, false),
    ('InseeAddress', 'Jean Fromage' ,'','','5 rue des Croissants' ,'','06000 Nice' ,'France', false, null, null, null, null, false);

INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-NATIONAL', 'National organizational unit', 'NATIONAL', null);
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-NORTH', 'North region organizational unit', 'LOCAL', 'OU-NATIONAL');
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-SOUTH', 'South region organizational unit', 'LOCAL', 'OU-NATIONAL');
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-WEST', 'West region organizational unit', 'LOCAL', 'OU-NATIONAL');

INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number) VALUES
    ('INTW1', 'margie.lucas@ou.com', 'Margie', 'Lucas', '+3391231231230'),
    ('INTW2', 'carlton.campbell@ou.com', 'Carlton', 'Campbell', '+3391231231231'),
    ('INTW3', 'gerald.edwards@ou.com', 'Gerald', 'Edwards', '+3391231231231'),
    ('INTW4', 'melody.grant@ou.com', 'Melody', 'Grant', '+3391231231231'),
    ('GUEST', 'guest.guest@ou.com', 'GUEST', 'GUEST', '+3391231231231');

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
    ('InseeSampleIdentifier', '25', 25, '1', 25, '25', 25, 25, 25, 25, 25, 1),
    ('InseeSampleIdentifier', '26', 26, '1', 26, '26', 26, 26, 26, 26, 26, 1),
    ('InseeSampleIdentifier', '27', 27, '1', 27, '27', 27, 27, 27, 27, 27, 2),
    ('InseeSampleIdentifier', '28', 28, '1', 28, '28', 28, 28, 28, 28, 28, 3),
    ('InseeSampleIdentifier', '29', 29, '2', 29, '29', 29, 29, 29, 29, 29, 1),
    ('InseeSampleIdentifier', '30', 30, '2', 30, '30', 30, 30, 30, 30, 30, 1),
    ('InseeSampleIdentifier', '31', 31, '2', 31, '31', 31, 31, 31, 31, 31, 2),
    ('InseeSampleIdentifier', '32', 32, '2', 32, '32', 32, 32, 32, 32, 32, 1),
    ('InseeSampleIdentifier', '33', 33, '2', 33, '33', 33, 33, 33, 33, 33, 1);

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
    ('OU-NORTH', 'SIMPSONS2020X00',
    EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
    EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
    true, 'north-simpsons@nooneknows.fr', '0321234567'),
    ('OU-NORTH', 'VQS2021X00',
    EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
    EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
    true, 'north-vqs@nooneknows.fr', ''),

    ('OU-SOUTH', 'VQS2021X00',
    EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
    EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
    true, 'south-vqs@nooneknows.fr', ''),

    ('OU-SOUTH', 'SIMPSONS2020X00',
    EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
    EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
    true, 'south-simpsons@nooneknows.fr', '0123456789'),

    ('OU-SOUTH', 'ZCLOSEDX00',
    EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '5 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '6 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '7 days') * 1000,
    true, 'south-zclosed@nooneknows.fr', ''),

    ('OU-WEST', 'ZCLOSEDX00',
    EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '5 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '6 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '7 days') * 1000,
    true, 'west-zclosed@nooneknows.fr', ''),

    ('OU-SOUTH', 'XCLOSEDX00',
    EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '5 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '6 days') * 1000,
    EXTRACT(EPOCH FROM NOW() - INTERVAL '7 days') * 1000,
    true, 'south-xclosed@nooneknows.fr', '');

INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '11', 'business-id-11', TRUE,  a.id, 'SIMPSONS2020X00', 'GUEST', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Ted Farmer' AND s.bs='11';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '12', 'business-id-12', TRUE,  a.id, 'SIMPSONS2020X00', 'GUEST', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Cecilia Ortega' AND s.bs='12';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '13', 'business-id-13', FALSE, a.id, 'SIMPSONS2020X00', 'GUEST', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Claude Watkins' AND s.bs='13';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '14', 'business-id-14', FALSE, a.id, 'SIMPSONS2020X00', 'GUEST', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Veronica Gill' AND s.bs='14';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '20', 'business-id-20', FALSE, a.id, 'VQS2021X00', 'GUEST', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Christine Aguilar' AND s.bs='20';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '21', 'business-id-21', FALSE, a.id, 'VQS2021X00', 'GUEST', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Louise Walker' AND s.bs='21';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '22', 'business-id-22', FALSE, a.id, 'VQS2021X00', 'GUEST', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Anthony Bennett' AND s.bs='22';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '23', 'business-id-23', FALSE, a.id, 'VQS2021X00', 'GUEST', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Christopher Lewis' AND s.bs='23';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '24', 'business-id-24', TRUE,  a.id, 'SIMPSONS2020X00', NULL, s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Laurent Neville' AND s.bs='24';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '25', 'business-id-25', TRUE,  a.id, 'SIMPSONS2020X00', 'INTW1', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Alain Thé' AND s.bs='25';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '26', 'business-id-26', TRUE,  a.id, 'SIMPSONS2020X00', 'INTW1', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Brie Savarin' AND s.bs='26';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '27', 'business-id-27', FALSE,  a.id, 'SIMPSONS2020X00', 'INTW2', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Cécile Houte' AND s.bs='27';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '28', 'business-id-28', FALSE,  a.id, 'SIMPSONS2020X00', 'INTW3', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Dan Tifrice' AND s.bs='28';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '29', 'business-id-29', FALSE,  a.id, 'VQS2021X00', 'INTW1', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Émile Pates' AND s.bs='29';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '30', 'business-id-30', FALSE,  a.id, 'VQS2021X00', 'INTW2', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='François Appétit' AND s.bs='30';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '31', 'business-id-31', FALSE,  a.id, 'VQS2021X00', 'INTW4', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Gérard Dine' AND s.bs='31';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '32', 'business-id-32', FALSE,  a.id, 'VQS2021X00', 'INTW4', s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Hélène Gume' AND s.bs='32';
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) SELECT '33', 'business-id-33', TRUE,  a.id, 'SIMPSONS2020X00', NULL, s.id, 'OU-NORTH' FROM address a, sample_identifier s WHERE a.l1='Jean Fromage' AND s.bs='33';

INSERT INTO public.person (email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES
    ('test@test.com',TRUE, 'Ted', 'Farmer', 11111111, 0, TRUE, '11'),
    ('test@test.com', TRUE,'Cecilia', 'Ortega', 11111111, 1, TRUE, '12'),
    ('test@test.com', TRUE,'Claude', 'Watkins', 11111111, 0, TRUE, '13'),
    ('test@test.com', TRUE,'Veronica', 'Baker', 11111111, 1, TRUE, '14'),
    ('test@test.com', TRUE,'Christine', 'Aguilar', 11111111, 1, FALSE, '11'),
    ('test@test.com', TRUE,'Louise', 'Walker', 11111111, 1, FALSE, '11'),
    ('test@test.com', TRUE,'Anthony', 'Bennett', 11111111, 0, FALSE, '12'),
    ('test@test.com', TRUE,'Christopher', 'Lewis', 11111111, 0, FALSE, '14'),
    ('test@test.com', TRUE,'Harriette', 'Raymond', 11111111, 0, TRUE, '20'),
    ('test@test.com', TRUE,'Aimée', 'Lamothe', 11111111, 0, TRUE, '21'),
    ('test@test.com', TRUE,'Perrin', 'Blanchard', 11111111, 0, TRUE, '22'),
    ('test@test.com', TRUE,'Artus', 'Arnoux', 11111111, 0, TRUE, '23'),
    ('test@test.com', TRUE,'Laurent', 'Neville', 11111111, 0, TRUE, '24'),
    ('test@test.com', TRUE, 'Harry', 'Cover', 11111111, 1, FALSE, '25'),
    ('test@test.com', TRUE, 'Ella', 'Gance', 11111111, 0, FALSE, '25'),
    ('test@test.com', TRUE, 'Jean', 'Neige', 11111111, 0, FALSE, '26'),
    ('test@test.com', TRUE, 'Phil', 'Harmonie', 11111111, 0, FALSE, '28'),
    ('test@test.com', TRUE, 'Alain', 'Thé', 11111111, 0, TRUE, '25'),
    ('test@test.com', TRUE, 'Brie', 'Savarin', 11111111, 1, TRUE, '26'),
    ('test@test.com', TRUE, 'Cécile', 'Houte', 11111111, 0, TRUE, '27'),
    ('test@test.com', TRUE, 'Dan', 'Tifrice', 11111111, 1, TRUE, '28'),
    ('test@test.com', TRUE, 'Émile', 'Pates', 11111111, 1, TRUE, '29'),
    ('test@test.com', TRUE, 'François', 'Appétit', 11111111, 1, TRUE, '30'),
    ('test@test.com', TRUE, 'Gérard', 'Dine', 11111111, 0, TRUE, '31'),
    ('test@test.com', TRUE, 'Hélène', 'Gume', 11111111, 0, TRUE, '32'),
    ('test@test.com', TRUE, 'Jean', 'Fromage', 11111111, 0, TRUE, '33');



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
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Harry' and p.last_name='Cover' and p.survey_unit_id='25';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT FALSE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Ella' and p.last_name='Gance' and p.survey_unit_id='25';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Jean' and p.last_name='Neige' and p.survey_unit_id='26';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Phil' and p.last_name='Harmonie' and p.survey_unit_id='28';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Alain' and p.last_name='Thé' and p.survey_unit_id='25';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Brie' and p.last_name='Savarin' and p.survey_unit_id='26';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Cécile' and p.last_name='Houte' and p.survey_unit_id='27';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Dan' and p.last_name='Tifrice' and p.survey_unit_id='28';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Émile' and p.last_name='Pates' and p.survey_unit_id='29';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='François' and p.last_name='Appétit' and p.survey_unit_id='30';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Gérard' and p.last_name='Dine' and p.survey_unit_id='31';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Hélène' and p.last_name='Gume' and p.survey_unit_id='32';
INSERT INTO public.phone_number (favorite, number, source, person_id) SELECT TRUE,'+33677542802', 0,  p.id FROM person p WHERE p.first_name='Jean' and p.last_name='Fromage' and p.survey_unit_id='33';

INSERT INTO public.state (date, type, survey_unit_id) VALUES
    (111112111,'VIN', '11'),
    (110111111,'NNS', '11'),
    (111111111,'TBR', '12'),
    (111111111,'TBR', '13'),
    (111111111,'TBR', '14'),
    (101111111,'TBR', '11'),
    (101111111,'TBR', '24'),
    (1590504478334, 'VIC', '20'),
    (1590504478334, 'VIC', '21'),
    (1590504478334, 'FIN', '22'),
    (1590504478334, 'VIC', '23'),
    (111112111,'VIN', '25'),
    (110111111,'NNS', '25'),
    (111111111,'TBR', '26'),
    (111111111,'TBR', '27'),
    (111111111,'TBR', '28'),
    (101111111,'TBR', '25'),
    (101111111,'TBR', '33'),
    (1590504478334, 'VIC', '29'),
    (1590504478334, 'VIC', '30'),
    (1590504478334, 'FIN', '31'),
    (1590504478334, 'VIC', '32');

INSERT INTO public.contact_outcome (date, type, survey_unit_id) VALUES
    (1590504478334, 'DUK', '24'),
    (1590504478334, 'DUK', '33');

INSERT INTO public.comment (type, value, survey_unit_id) VALUES
    ('INTERVIEWER', 'un commentaire', '13'),
    ('INTERVIEWER', 'un commentaire', '27');

INSERT INTO closing_cause (date, type, survey_unit_id) VALUES
    (EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000, 'NPI', '11'),
    (EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000, 'NPI', '25');


INSERT INTO public.identification (survey_unit_id, identification,access,situation,category,occupant) VALUES
    ('11', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED'),
    ('21', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED'),
    ('25', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED'),
    ('30', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED');

INSERT INTO public.communication_template (meshuggah_id, medium, type, campaign_id) VALUES
    ('mesh1', 'EMAIL', 'REMINDER', 'SIMPSONS2020X00'),
    ('mesh2', 'LETTER', 'NOTICE', 'SIMPSONS2020X00'),
    ('mesh3', 'EMAIL', 'REMINDER', 'VQS2021X00'),
    ('mesh4', 'LETTER', 'NOTICE', 'VQS2021X00'),
    ('mesh5', 'EMAIL', 'NOTICE', 'VQS2021X00');

INSERT INTO public.communication_request (survey_unit_id, emitter, reason, communication_template_id) VALUES
    ('11', 'INTERVIEWER', 'REFUSAL', 1),
    ('11', 'INTERVIEWER', 'UNREACHABLE', 2),
    ('20', 'INTERVIEWER', 'REFUSAL', 3),
    ('20', 'INTERVIEWER', 'UNREACHABLE', 4);

INSERT INTO public.communication_request_status (communication_request_id, status, date) VALUES
    (1, 'INITIATED', 1721903754305),
    (1, 'READY', 1721903755305),
    (1, 'SUBMITTED', 1721903756305),
    (2, 'INITIATED', 1721903754305),
    (2, 'READY', 1721903756310),
    (3, 'INITIATED', 1721903754205),
    (4, 'INITIATED', 1721903754205);