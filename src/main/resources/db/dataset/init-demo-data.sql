--changeset davdarras:init-demo-data context:demo

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

INSERT INTO public.address (id, dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES
    (1,  'InseeAddress', 'Ted Farmer',            '', '', '1 rue de la gare',              '', '29270 Carhaix',        'France', true,  'Bat. C', 'Etg 4', 'Porte 48',   'Escalier B', true),
    (2,  'InseeAddress', 'Cecilia Ortega',        '', '', '2 place de la mairie',          '', '90000 Belfort',       'France', false, null,     null,    null,         null,       false),
    (3,  'InseeAddress', 'Claude Watkins',        '', '', '3 avenue de la République',     '', '32230 Marciac',       'France', false, null,     null,    null,         null,       false),
    (4,  'InseeAddress', 'Veronica Gill',         '', '', '4 chemin du ruisseau',          '', '44190 Clisson',       'France', false, null,     null,    null,         null,       false),
    (5,  'InseeAddress', 'Christine Aguilar',     '', '', '5 rue de l''école',             '', '59620 Aulnoye-Aimeries','France', false, null,     null,    null,         null,       false),
    (6,  'InseeAddress', 'Louise Walker',         '', '', '6 impasse du lac',              '', '38200 Vienne',        'France', false, null,     null,    null,         null,       false),
    (7,  'InseeAddress', 'Anthony Bennett',       '', '', '7 avenue de la Liberté',        '', '62000 Arras',         'France', false, null,     null,    null,         null,       false),
    (8,  'InseeAddress', 'Christopher Lewis',     '', '', '8 route du moulin',             '', '35000 Rennes',        'France', false, null,     null,    null,         null,       false),
    (9,  'InseeAddress', 'Laurent Neville',       '', '', '5 route du sapin',              '', '35000 Rennes',        'France', false, null,     null,    null,         null,       false),
    (10, 'InseeAddress', 'Alain Thé',             '', '', '7 rue des Infusions',           '', '75001 Paris',         'France', false, null,     null,    null,         null,       false),
    (11, 'InseeAddress', 'Brie Savarin',          '', '', '15 avenue des Fromages',        '', '69002 Lyon',          'France', false, null,     null,    null,         null,       false),
    (12, 'InseeAddress', 'Cécile Houte',          '', '', '8 impasse des Aromates',        '', '13003 Marseille',     'France', false, null,     null,    null,         null,       false),
    (13, 'InseeAddress', 'Dan Tifrice',           '', '', '12 chemin du Dentifrice',       '', '33000 Bordeaux',      'France', false, null,     null,    null,         null,       false),
    (14, 'InseeAddress', 'Émile Pates',           '', '', '3 place des Pâtes',             '', '59000 Lille',         'France', false, null,     null,    null,         null,       false),
    (15, 'InseeAddress', 'François Appétit',      '', '', '22 rue de la Faim',             '', '67000 Strasbourg',    'France', false, null,     null,    null,         null,       false),
    (16, 'InseeAddress', 'Gérard Dine',           '', '', '9 allée des Gourmands',         '', '44000 Nantes',        'France', false, null,     null,    null,         null,       false),
    (17, 'InseeAddress', 'Hélène Gume',           '', '', '18 boulevard des Légumes',      '', '31000 Toulouse',      'France', false, null,     null,    null,         null,       false),
    (18, 'InseeAddress', 'Jean Fromage',          '', '', '5 rue des Croissants',          '', '06000 Nice',          'France', false, null,     null,    null,         null,       false),
    (19, 'InseeAddress', 'Isabelle Moreau',       '', '', '10 rue des Fleurs',             '', '75002 Paris',         'France', false, null,     null,    null,         null,       false),
    (20, 'InseeAddress', 'Julien Dupont',         '', '', '20 avenue Victor Hugo',         '', '75008 Paris',         'France', false, null,     null,    null,         null,       false),
    (21, 'InseeAddress', 'Sophie Martin',         '', '', '30 boulevard Saint-Germain',    '', '75005 Paris',         'France', false, null,     null,    null,         null,       false),
    (22, 'InseeAddress', 'Marc Lefevre',          '', '', '40 quai de Seine',              '', '75007 Paris',         'France', false, null,     null,    null,         null,       false),
    (23, 'InseeAddress', 'Nathalie Bernard',      '', '', '50 rue de Rivoli',              '', '75004 Paris',         'France', false, null,     null,    null,         null,       false),
    (24, 'InseeAddress', 'Olivier Girard',        '', '', '60 rue de la Paix',             '', '75002 Paris',         'France', false, null,     null,    null,         null,       false),
    (25, 'InseeAddress', 'Camille Renaud',        '', '', '70 rue de Belleville',          '', '75020 Paris',         'France', false, null,     null,    null,         null,       false),
    (26, 'InseeAddress', 'Pauline Durand',        '', '', '80 rue de la République',       '', '69001 Lyon',          'France', false, null,     null,    null,         null,       false),
    (27, 'InseeAddress', 'Antoine Morel',         '', '', '90 avenue Jean Jaurès',         '', '69003 Lyon',          'France', false, null,     null,    null,         null,       false),
    (28, 'InseeAddress', 'Marine Lambert',        '', '', '100 rue Victor Hugo',           '', '69002 Lyon',          'France', false, null,     null,    null,         null,       false),
    (29, 'InseeAddress', 'Alexandre Faure',       '', '', '110 boulevard des Capucines',   '', '33000 Bordeaux',      'France', false, null,     null,    null,         null,       false),
    (30, 'InseeAddress', 'Elodie Renault',        '', '', '120 rue Sainte-Catherine',      '', '33000 Bordeaux',      'France', false, null,     null,    null,         null,       false),
    (31, 'InseeAddress', 'Maxime Petit',          '', '', '130 chemin de la Lune',         '', '13004 Marseille',     'France', false, null,     null,    null,         null,       false),
    (32, 'InseeAddress', 'Amélie Gautier',        '', '', '140 rue du Soleil',             '', '13001 Marseille',     'France', false, null,     null,    null,         null,       false),
    (33, 'InseeAddress', 'Damien Roux',           '', '', '150 avenue des Étoiles',        '', '06000 Nice',          'France', false, null,     null,    null,         null,       false),
    (34, 'InseeAddress', 'Claire Dubois',         '', '', '160 boulevard de la Mer',       '', '06200 Nice',          'France', false, null,     null,    null,         null,       false),
    (35, 'InseeAddress', 'Hugo Simon',            '', '', '170 rue de l''Industrie',       '', '67000 Strasbourg',    'France', false, null,     null,    null,         null,       false),
    (36, 'InseeAddress', 'Chloé Lemoine',         '', '', '180 rue des Arts',              '', '44000 Nantes',        'France', false, null,     null,    null,         null,       false),
    (37, 'InseeAddress', 'Romain Bertrand',       '', '', '190 avenue du Général de Gaulle','', '34000 Montpellier',   'France', false, null,     null,    null,         null,       false),
    (38, 'InseeAddress', 'Alice Renault',         '', '', '200 boulevard de la Liberté',   '', '34000 Montpellier',   'France', false, null,     null,    null,         null,       false),
    (39, 'InseeAddress', 'Guillaume Faure',       '', '', '210 rue des Entrepreneurs',     '', '38000 Grenoble',      'France', false, null,     null,    null,         null,       false),
    (40, 'InseeAddress', 'Sébastien Dupuis',      '', '', '40 rue de la République',       '', '75000 Paris',         'France', false, null,     null,    null,         null,       false),
    (41, 'InseeAddress', 'Marine Lefevre',        '', '', '41 rue de la République',       '', '75001 Paris',         'France', false, null,     null,    null,         null,       false),
    (42, 'InseeAddress', 'Thierry Bernard',       '', '', '42 avenue de la Liberté',       '', '69000 Lyon',          'France', false, null,     null,    null,         null,       false),
    (43, 'InseeAddress', 'Sylvie Martin',         '', '', '43 boulevard Saint-Germain',    '', '13000 Marseille',     'France', false, null,     null,    null,         null,       false),
    (44, 'InseeAddress', 'Laurence Girard',       '', '', '44 chemin du Moulin',           '', '34000 Montpellier',    'France', false, null,     null,    null,         null,       false),
    (45, 'InseeAddress', 'Dominique Moreau',      '', '', '45 impasse du Lac',             '', '31000 Toulouse',      'France', false, null,     null,    null,         null,       false),
    (46, 'InseeAddress', 'Juliette Roussel',      '', '', '46 route du Soleil',            '', '44000 Nantes',        'France', false, null,     null,    null,         null,       false),
    (47, 'InseeAddress', 'Pascal Dubois',         '', '', '47 place de la Mairie',         '', '67000 Strasbourg',     'France', false, null,     null,    null,         null,       false),
    (48, 'InseeAddress', 'Amandine Perrin',       '', '', '48 allée des Fleurs',           '', '35000 Rennes',         'France', false, null,     null,    null,         null,       false),
    (49, 'InseeAddress', 'Nicolas Lambert',       '', '', '49 rue des Sapins',             '', '29000 Quimper',        'France', false, null,     null,    null,         null,       false),
    (50, 'InseeAddress', 'Bruno Simon',           '', '', '50 avenue de la Gare',          '', '21000 Dijon',          'France', false, null,     null,    null,         null,       false),
    (51, 'InseeAddress', 'Véronique Petit',       '', '', '51 rue des Ecoles',             '', '14000 Caen',           'France', false, null,     null,    null,         null,       false),
    (52, 'InseeAddress', 'Philippe Garnier',      '', '', '52 rue de l''Industrie',        '', '76000 Rouen',          'France', false, null,     null,    null,         null,       false),
    (53, 'InseeAddress', 'Catherine Renault',     '', '', '53 boulevard de la Mer',        '', '80000 Amiens',         'France', false, null,     null,    null,         null,       false),
    (54, 'InseeAddress', 'Jacques Faure',         '', '', '54 chemin des Acacias',         '', '33000 Bordeaux',       'France', false, null,     null,    null,         null,       false);

INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-NATIONAL', 'National organizational unit', 'NATIONAL', null);
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-NORTH', 'North region organizational unit', 'LOCAL', 'OU-NATIONAL');
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-SOUTH', 'South region organizational unit', 'LOCAL', 'OU-NATIONAL');
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id) VALUES ('OU-WEST', 'West region organizational unit', 'LOCAL', 'OU-NATIONAL');

INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number) VALUES
    ('INTERV1', 'interviewer1@insee.fr', 'Isabelle', 'Interviewer 1', '+3391231231231'),
    ('INTERV2', 'interviewer2@insee.fr', 'Ingrid',   'Interviewer 2', '+3391231231232'),
    ('INTERV3', 'interviewer3@insee.fr', 'Isaac',    'Interviewer 3', '+3391231231233'),
    ('INTERV4', 'interviewer4@insee.fr', 'Isidore',  'Interviewer 4', '+3391231231234'),
    ('INTERV5', 'interviewer5@insee.fr', 'Irma',     'Interviewer 5', '+3391231231235');

INSERT INTO public.sample_identifier (id, dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES
    (1,  'InseeSampleIdentifier', '1',  1, '1',  1,  '11',  1,  1,  1,  1,  1, 1),
    (2,  'InseeSampleIdentifier', '2',  2, '1',  2,  '12',  2,  2,  2,  2,  2, 1),
    (3,  'InseeSampleIdentifier', '3',  3, '1',  3,  '13',  3,  3,  3,  3,  3, 2),
    (4,  'InseeSampleIdentifier', '4',  4, '1',  4,  '14',  4,  4,  4,  4,  4, 3),
    (5,  'InseeSampleIdentifier', '5',  5, '2',  5,  '15',  5,  5,  5,  5,  5, 1),
    (6,  'InseeSampleIdentifier', '6',  6, '2',  6,  '16',  6,  6,  6,  6,  6, 1),
    (7,  'InseeSampleIdentifier', '7',  7, '2',  7,  '17',  7,  7,  7,  7,  7, 2),
    (8,  'InseeSampleIdentifier', '8',  8, '2',  8,  '18',  8,  8,  8,  8,  8, 1),
    (9,  'InseeSampleIdentifier', '9',  9, '2',  9,  '19',  9,  9,  9,  9,  9, 1),
    (10, 'InseeSampleIdentifier', '10', 10, '1', 10,  '20', 10, 10, 10, 10, 10, 1),
    (11, 'InseeSampleIdentifier', '11', 11, '1', 11,  '21', 11, 11, 11, 11, 11, 1),
    (12, 'InseeSampleIdentifier', '12', 12, '1', 12,  '22', 12, 12, 12, 12, 12, 2),
    (13, 'InseeSampleIdentifier', '13', 13, '1', 13,  '23', 13, 13, 13, 13, 13, 3),
    (14, 'InseeSampleIdentifier', '14', 14, '2', 14,  '24', 14, 14, 14, 14, 14, 1),
    (15, 'InseeSampleIdentifier', '15', 15, '2', 15,  '25', 15, 15, 15, 15, 15, 1),
    (16, 'InseeSampleIdentifier', '16', 16, '2', 16,  '26', 16, 16, 16, 16, 16, 2),
    (17, 'InseeSampleIdentifier', '17', 17, '2', 17,  '27', 17, 17, 17, 17, 17, 1),
    (18, 'InseeSampleIdentifier', '18', 18, '2', 18,  '28', 18, 18, 18, 18, 18, 1),
    (19, 'InseeSampleIdentifier', '19', 19, '1', 19,  '29', 19, 19, 19, 19, 19, 1),
    (20, 'InseeSampleIdentifier', '20', 20, '1', 20,  '30', 20, 20, 20, 20, 20, 1),
    (21, 'InseeSampleIdentifier', '21', 21, '1', 21,  '31', 21, 21, 21, 21, 21, 2),
    (22, 'InseeSampleIdentifier', '22', 22, '1', 22,  '32', 22, 22, 22, 22, 22, 3),
    (23, 'InseeSampleIdentifier', '23', 23, '1', 23,  '33', 23, 23, 23, 23, 23, 1),
    (24, 'InseeSampleIdentifier', '24', 24, '1', 24,  '34', 24, 24, 24, 24, 24, 1),
    (25, 'InseeSampleIdentifier', '25', 25, '1', 25,  '35', 25, 25, 25, 25, 25, 2),
    (26, 'InseeSampleIdentifier', '26', 26, '1', 26,  '36', 26, 26, 26, 26, 26, 1),
    (27, 'InseeSampleIdentifier', '27', 27, '1', 27,  '37', 27, 27, 27, 27, 27, 1),
    (28, 'InseeSampleIdentifier', '28', 28, '1', 28,  '38', 28, 28, 28, 28, 28, 1),
    (29, 'InseeSampleIdentifier', '29', 29, '1', 29,  '39', 29, 29, 29, 29, 29, 1),
    (30, 'InseeSampleIdentifier', '30', 30, '1', 30,  '40', 30, 30, 30, 30, 30, 2),
    (31, 'InseeSampleIdentifier', '31', 31, '1', 31,  '41', 31, 31, 31, 31, 31, 3),
    (32, 'InseeSampleIdentifier', '32', 32, '1', 32,  '42', 32, 32, 32, 32, 32, 1),
    (33, 'InseeSampleIdentifier', '33', 33, '1', 33,  '43', 33, 33, 33, 33, 33, 1),
    (34, 'InseeSampleIdentifier', '34', 34, '1', 34,  '44', 34, 34, 34, 34, 34, 2),
    (35, 'InseeSampleIdentifier', '35', 35, '1', 35,  '45', 35, 35, 35, 35, 35, 1),
    (36, 'InseeSampleIdentifier', '36', 36, '1', 36,  '46', 36, 36, 36, 36, 36, 1),
    (37, 'InseeSampleIdentifier', '37', 37, '1', 37,  '47', 37, 37, 37, 37, 37, 1),
    (38, 'InseeSampleIdentifier', '38', 38, '1', 38,  '48', 38, 38, 38, 38, 38, 1),
    (39, 'InseeSampleIdentifier', '39', 39, '1', 39,  '49', 39, 39, 39, 39, 39, 1),
    (40, 'InseeSampleIdentifier', '40', 40, '1', 40,  '50', 40, 40, 40, 40, 40, 1),
    (41, 'InseeSampleIdentifier', '41', 41, '1', 41,  '51', 41, 41, 41, 41, 41, 1),
    (42, 'InseeSampleIdentifier', '42', 42, '1', 42,  '52', 42, 42, 42, 42, 42, 1),
    (43, 'InseeSampleIdentifier', '43', 43, '1', 43,  '53', 43, 43, 43, 43, 43, 1),
    (44, 'InseeSampleIdentifier', '44', 44, '1', 44,  '54', 44, 44, 44, 44, 44, 1),
    (45, 'InseeSampleIdentifier', '45', 45, '1', 45,  '55', 45, 45, 45, 45, 45, 1),
    (46, 'InseeSampleIdentifier', '46', 46, '1', 46,  '56', 46, 46, 46, 46, 46, 1),
    (47, 'InseeSampleIdentifier', '47', 47, '1', 47,  '57', 47, 47, 47, 47, 47, 1),
    (48, 'InseeSampleIdentifier', '48', 48, '1', 48,  '58', 48, 48, 48, 48, 48, 1),
    (49, 'InseeSampleIdentifier', '49', 49, '1', 49,  '59', 49, 49, 49, 49, 49, 1),
    (50, 'InseeSampleIdentifier', '50', 50, '1', 50,  '60', 50, 50, 50, 50, 50, 1),
    (51, 'InseeSampleIdentifier', '51', 51, '1', 51,  '61', 51, 51, 51, 51, 51, 1),
    (52, 'InseeSampleIdentifier', '52', 52, '1', 52,  '62', 52, 52, 52, 52, 52, 1),
    (53, 'InseeSampleIdentifier', '53', 53, '1', 53,  '63', 53, 53, 53, 53, 53, 1),
    (54, 'InseeSampleIdentifier', '54', 54, '1', 54,  '64', 54, 54, 54, 54, 54, 1);

INSERT INTO public.USER (id, first_name, last_name, organization_unit_id) VALUES
    ('GESTIO1', 'Gertrude', 'Gestionnaire1', 'OU-NATIONAL'),
    ('GESTIO2', 'Gontrand', 'Gestionnaire2', 'OU-NORTH'),
    ('GESTIO3', 'Gerard', 'Gestionnaire3', 'OU-SOUTH'),
    ('GESTIO4', 'Gustave', 'Gestionnaire4', 'OU-WEST');

INSERT INTO public.campaign (id, label, email, identification_configuration, contact_attempt_configuration, contact_outcome_configuration) VALUES
    ('SIMPSONS2020X00', 'Survey on the Simpsons tv show 2020', 'first.email@test.com', 'IASCO', 'F2F', 'F2F'),
    ('VQS2021X00', 'Everyday life and health survey 2021', 'second.email@test.com', 'IASCO', 'TEL', 'TEL'),
    ('ZCLOSEDX00', 'Everyday life and health survey 2021', 'third.email@test.com', 'IASCO', 'F2F', 'F2F'),
    ('XCLOSEDX00', 'Everyday life and health survey 2021', 'fourth.email@test.com', 'IASCO', 'TEL', 'TEL'),
    ('AQV2022X00', 'Campagne qualité volaille en 2022', 'second.email@test.com', 'IASCO', 'F2F', 'F2F'),
    ('AQV2023X00', 'Campagne qualité volaille en 2023', 'second.email@test.com', 'IASCO', 'F2F', 'F2F'),
    ('AQV2024X00', 'Campagne qualité volaille en 2024', 'second.email@test.com', 'IASCO', 'F2F', 'F2F'),
    ('INDTEL2025X00', 'INDTEL campaign', 'third.email@test.com', 'INDTEL', 'TEL', 'TEL'),
    ('IASCO2025X00', 'IASCO campaign', 'third.email@test.com', 'IASCO', 'F2F', 'F2F'),
    ('NOIDENT2025X00', 'NOIDENT campaign', 'third.email@test.com', 'NOIDENT', NULL, NULL),
    ('HOUSEF2F2025X00', 'HOUSEF2F campaign', 'third.email@test.com', 'HOUSEF2F', 'F2F', 'F2F'),
    ('HOUSETEL2025X00', 'HOUSETEL campaign', 'third.email@test.com', 'HOUSETEL', 'TEL', 'TEL'),
    ('HOUSETELWSR2025X00', 'HOUSETELWSR campaign', 'third.email@test.com', 'HOUSETELWSR', 'TEL', 'TEL'),
    ('SRCVREINT2025X00', 'SRCVREINT campaign', 'third.email@test.com', 'SRCVREINT', NULL, NULL),
    ('INDTELNOR2025X00', 'INDTELNOR campaign', 'third.email@test.com', 'INDTELNOR', 'TEL', 'TEL'),
    ('INDF2F2025X00', 'INDF2F campaign', 'third.email@test.com', 'INDF2F', 'F2F', 'F2F'),
    ('INDF2FNOR2025X00', 'INDF2F campaign', 'third.email@test.com', 'INDF2FNOR', 'F2F', 'F2F');

INSERT INTO public.preference (id_user, id_campaign) VALUES
    ('GESTIO1', 'SIMPSONS2020X00'),
    ('GESTIO1', 'AQV2023X00'),
    ('GESTIO2', 'VQS2021X00'),
    ('GESTIO3', 'ZCLOSEDX00'),
    ('GESTIO4', 'XCLOSEDX00');


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
    true, 'south-xclosed@nooneknows.fr', ''),
    
    ('OU-NORTH', 'AQV2022X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'aqv2022@nooneknows.fr', '0321234567'),
    
    ('OU-NORTH', 'AQV2023X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'aqv2023@nooneknows.fr', '0321234567'),
        
    ('OU-NORTH', 'AQV2024X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'aqv2024@nooneknows.fr', '0321234567'),
        
    ('OU-NORTH', 'INDF2F2025X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'indf2f@nooneknows.fr', '0321234567'),

    ('OU-NORTH', 'INDF2FNOR2025X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'indf2fnor@nooneknows.fr', '0321234567'),
        
    ('OU-NORTH', 'INDTELNOR2025X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'indtelnor@nooneknows.fr', '0321234567'),
        
    ('OU-NORTH', 'SRCVREINT2025X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'srcvreint@nooneknows.fr', '0321234567'),
        
    ('OU-NORTH', 'HOUSETELWSR2025X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'housetelwsr@nooneknows.fr', '0321234567'),
        
    ('OU-NORTH', 'HOUSETEL2025X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'housetel@nooneknows.fr', '0321234567'),
        
    ('OU-NORTH', 'HOUSEF2F2025X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'housef2fs@nooneknows.fr', '0321234567'),
        
    ('OU-NORTH', 'NOIDENT2025X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'noident@nooneknows.fr', '0321234567'),
        
    ('OU-NORTH', 'IASCO2025X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'iasco@nooneknows.fr', '0321234567'),
        
    ('OU-NORTH', 'INDTEL2025X00',
        EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
        EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
        EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
        true, 'indtel@nooneknows.fr', '0321234567');

INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) VALUES
    ('11', 'business-id-11', TRUE, 1, 'SIMPSONS2020X00', 'INTERV5', 1, 'OU-NORTH'),
    ('12', 'business-id-12', TRUE, 2, 'SIMPSONS2020X00', 'INTERV5', 2, 'OU-NORTH'),
    ('13', 'business-id-13', FALSE, 3, 'SIMPSONS2020X00', 'INTERV5', 3, 'OU-NORTH'),
    ('14', 'business-id-14', FALSE, 4, 'SIMPSONS2020X00', 'INTERV5', 4, 'OU-NORTH'),
    ('20', 'business-id-20', FALSE, 5, 'VQS2021X00', 'INTERV5', 5, 'OU-NORTH'),
    ('21', 'business-id-21', FALSE, 6, 'VQS2021X00', 'INTERV5', 6, 'OU-NORTH'),
    ('22', 'business-id-22', FALSE, 7, 'VQS2021X00', 'INTERV5', 7, 'OU-NORTH'),
    ('23', 'business-id-23', FALSE, 8, 'VQS2021X00', 'INTERV5', 8, 'OU-NORTH'),
    ('24', 'business-id-24', TRUE, 9, 'SIMPSONS2020X00', NULL, 9, 'OU-NORTH'),
    ('25', 'business-id-25', TRUE, 10, 'SIMPSONS2020X00', 'INTERV1', 10, 'OU-NORTH'),
    ('26', 'business-id-26', TRUE, 11, 'SIMPSONS2020X00', 'INTERV1', 11, 'OU-NORTH'),
    ('27', 'business-id-27', FALSE, 12, 'SIMPSONS2020X00', 'INTERV2', 12, 'OU-NORTH'),
    ('28', 'business-id-28', FALSE, 13, 'SIMPSONS2020X00', 'INTERV3', 13, 'OU-NORTH'),
    ('29', 'business-id-29', FALSE, 14, 'VQS2021X00', 'INTERV1', 14, 'OU-NORTH'),
    ('30', 'business-id-30', FALSE, 15, 'VQS2021X00', 'INTERV2', 15, 'OU-NORTH'),
    ('31', 'business-id-31', FALSE, 16, 'VQS2021X00', 'INTERV4', 16, 'OU-NORTH'),
    ('32', 'business-id-32', FALSE, 17, 'VQS2021X00', 'INTERV4', 17, 'OU-NORTH'),
    ('33', 'business-id-33', TRUE, 18, 'SIMPSONS2020X00', NULL, 18, 'OU-NORTH'),
    ('PROTO01', 'business-id-proto01', TRUE, 19, 'AQV2023X00', 'INTERV5', 19, 'OU-NORTH'),
    ('PROTO02', 'business-id-proto02', TRUE, 20, 'AQV2023X00', 'INTERV5', 20, 'OU-NORTH'),
    ('PROTO03', 'business-id-proto03', FALSE, 21, 'AQV2023X00', 'INTERV5', 21, 'OU-NORTH'),
    ('PROTO04', 'business-id-proto04', FALSE, 22, 'AQV2023X00', 'INTERV5', 22, 'OU-NORTH'),
    ('PROTO05', 'business-id-proto05', FALSE, 23, 'AQV2023X00', 'INTERV5', 23, 'OU-NORTH'),
    ('PROTO06', 'business-id-proto06', FALSE, 24, 'AQV2023X00', 'INTERV5', 24, 'OU-NORTH'),
    ('PROTO07', 'business-id-proto07', FALSE, 25, 'AQV2023X00', 'INTERV5', 25, 'OU-NORTH'),
    ('PROTO08', 'business-id-proto08', FALSE, 26, 'AQV2023X00', 'INTERV5', 26, 'OU-NORTH'),
    ('PROTO09', 'business-id-proto09', TRUE, 27, 'AQV2023X00', NULL, 27, 'OU-NORTH'),
    ('PROTO10', 'business-id-proto10', TRUE, 28, 'AQV2023X00', 'INTERV1', 28, 'OU-NORTH'),
    ('PROTO11', 'business-id-proto11', TRUE, 29, 'AQV2023X00', 'INTERV1', 29, 'OU-NORTH'),
    ('PROTO12', 'business-id-proto12', FALSE, 30, 'AQV2023X00', 'INTERV2', 30, 'OU-NORTH'),
    ('PROTO13', 'business-id-proto13', FALSE, 31, 'AQV2023X00', 'INTERV3', 31, 'OU-NORTH'),
    ('PROTO14', 'business-id-proto14', FALSE, 32, 'AQV2023X00', 'INTERV1', 32, 'OU-NORTH'),
    ('PROTO15', 'business-id-proto15', FALSE, 33, 'AQV2023X00', 'INTERV2', 33, 'OU-NORTH'),
    ('PROTO16', 'business-id-proto16', FALSE, 34, 'AQV2023X00', 'INTERV4', 34, 'OU-NORTH'),
    ('PROTO17', 'business-id-proto17', FALSE, 35, 'AQV2023X00', 'INTERV4', 35, 'OU-NORTH'),
    ('PROTO18', 'business-id-proto18', TRUE, 36, 'AQV2023X00', NULL, 36, 'OU-NORTH'),
    ('PROTO19', 'business-id-proto19', TRUE, 37, 'AQV2023X00', NULL, 37, 'OU-NORTH'),
    ('PROTO20', 'business-id-proto20', TRUE, 38, 'AQV2023X00', NULL, 38, 'OU-NORTH'),
    ('PROTO21', 'business-id-proto21', TRUE, 39, 'AQV2024X00', NULL, 39, 'OU-NORTH'),
    ('SABIANE01', 'business-id-sabiane01', TRUE, 40, 'INDTEL2025X00', 'INTERV5', 40, 'OU-NORTH'),
    ('SABIANE02', 'business-id-sabiane02', TRUE, 41, 'INDTEL2025X00', 'INTERV5', 41, 'OU-NORTH'),
    ('SABIANE03', 'business-id-sabiane03', TRUE, 42, 'IASCO2025X00', 'INTERV5', 42, 'OU-NORTH'),
    ('SABIANE04', 'business-id-sabiane04', TRUE, 43, 'IASCO2025X00', 'INTERV5', 43, 'OU-NORTH'),
    ('SABIANE05', 'business-id-sabiane05', TRUE, 44, 'NOIDENT2025X00', 'INTERV5', 44, 'OU-NORTH'),
    ('SABIANE06', 'business-id-sabiane06', TRUE, 45, 'HOUSEF2F2025X00', 'INTERV5', 45, 'OU-NORTH'),
    ('SABIANE07', 'business-id-sabiane07', TRUE, 46, 'HOUSETEL2025X00', 'INTERV5', 46, 'OU-NORTH'),
    ('SABIANE08', 'business-id-sabiane08', TRUE, 47, 'HOUSETELWSR2025X00', 'INTERV5', 47, 'OU-NORTH'),
    ('SABIANE09', 'business-id-sabiane09', TRUE, 48, 'SRCVREINT2025X00', 'INTERV5', 48, 'OU-NORTH'),
    ('SABIANE10', 'business-id-sabiane10', TRUE, 49, 'INDTELNOR2025X00', 'INTERV5', 49, 'OU-NORTH'),
    ('SABIANE11', 'business-id-sabiane11', TRUE, 50, 'INDF2F2025X00', 'INTERV5', 50, 'OU-NORTH'),
    ('SABIANE12', 'business-id-sabiane12', TRUE, 51, 'INDF2F2025X00', 'INTERV5', 51, 'OU-NORTH'),
    ('SABIANE13', 'business-id-sabiane13', TRUE, 52, 'HOUSEF2F2025X00', 'INTERV5', 52, 'OU-NORTH'),
    ('SABIANE14', 'business-id-sabiane14', TRUE, 53, 'HOUSETEL2025X00', 'INTERV5', 53, 'OU-NORTH'),
    ('SABIANE15', 'business-id-sabiane15', TRUE, 54, 'HOUSETELWSR2025X00', 'INTERV5', 54, 'OU-NORTH');

INSERT INTO public.person (id, email, favorite_email, first_name, last_name, birthdate, title, privileged, survey_unit_id) VALUES
    (1,  'test@test.com', TRUE, 'Ted',         'Farmer',      315532800000, 0, TRUE,  '11'),
    (2,  'test@test.com', TRUE, 'Cecilia',     'Ortega',      325242830770, 1, TRUE,  '12'),
    (3,  'test@test.com', TRUE, 'Claude',      'Watkins',     334952861540, 0, TRUE,  '13'),
    (4,  'test@test.com', TRUE, 'Veronica',    'Baker',       344662892310, 1, TRUE,  '14'),
    (5,  'test@test.com', TRUE, 'Christine',   'Aguilar',     354372923080, 1, FALSE, '11'),
    (6,  'test@test.com', TRUE, 'Louise',      'Walker',      364082953850, 1, FALSE, '11'),
    (7,  'test@test.com', TRUE, 'Anthony',     'Bennett',     373792984620, 0, FALSE, '12'),
    (8,  'test@test.com', TRUE, 'Christopher', 'Lewis',       383503015390, 0, FALSE, '14'),
    (9,  'test@test.com', TRUE, 'Harriette',   'Raymond',     393213046160, 1, TRUE,  '20'),
    (10, 'test@test.com', TRUE, 'Aimée',       'Lamothe',     402923076930, 0, TRUE,  '21'),
    (11, 'test@test.com', TRUE, 'Perrin',      'Blanchard',   412633107700, 0, TRUE,  '22'),
    (12, 'test@test.com', TRUE, 'Artus',       'Arnoux',      422343138470, 0, TRUE,  '23'),
    (13, 'test@test.com', TRUE, 'Laurent',     'Neville',     432053169240, 0, TRUE,  '24'),
    (14, 'test@test.com', TRUE, 'Harry',       'Cover',       441763200010, 1, FALSE, '25'),
    (15, 'test@test.com', TRUE, 'Ella',        'Gance',       451473230780, 1, FALSE, '25'),
    (16, 'test@test.com', TRUE, 'Jean',        'Neige',       461183261550, 0, FALSE, '26'),
    (17, 'test@test.com', TRUE, 'Phil',        'Harmonie',    470893292320, 0, FALSE, '28'),
    (18, 'test@test.com', TRUE, 'Alain',       'Thé',         480603323090, 0, TRUE,  '25'),
    (19, 'test@test.com', TRUE, 'Brie',        'Savarin',     490313353860, 1, TRUE,  '26'),
    (20, 'test@test.com', TRUE, 'Cécile',      'Houte',       500023384630, 1, TRUE,  '27'),
    (21, 'test@test.com', TRUE, 'Dan',         'Tifrice',     509733415400, 0, TRUE,  '28'),
    (22, 'test@test.com', TRUE, 'Émile',       'Pates',       519443446170, 0, TRUE,  '29'),
    (23, 'test@test.com', TRUE, 'François',    'Appétit',     529153476940, 0, TRUE,  '30'),
    (24, 'test@test.com', TRUE, 'Gérard',      'Dine',        538863507710, 0, TRUE,  '31'),
    (25, 'test@test.com', TRUE, 'Hélène',      'Gume',        548573538480, 1, TRUE,  '32'),
    (26, 'test@test.com', TRUE, 'Jean',        'Fromage',     558283569250, 0, TRUE,  '33'),
    (27, 'test@test.com', TRUE, 'Isabelle',    'Moreau',      567993600020, 1, TRUE,  'PROTO01'),
    (28, 'test@test.com', TRUE, 'Julien',      'Dupont',      577703630790, 0, TRUE,  'PROTO02'),
    (29, 'test@test.com', TRUE, 'Sophie',      'Martin',      587413661560, 1, TRUE,  'PROTO03'),
    (30, 'test@test.com', TRUE, 'Marc',        'Lefevre',     597123692330, 0, TRUE,  'PROTO04'),
    (31, 'test@test.com', TRUE, 'Nathalie',    'Bernard',     606833723100, 1, TRUE,  'PROTO05'),
    (32, 'test@test.com', TRUE, 'Olivier',     'Girard',      616543753870, 0, TRUE,  'PROTO06'),
    (33, 'test@test.com', TRUE, 'Camille',     'Renaud',      626253784640, 1, TRUE,  'PROTO07'),
    (34, 'test@test.com', TRUE, 'Pauline',     'Durand',      635963815410, 1, TRUE,  'PROTO08'),
    (35, 'test@test.com', TRUE, 'Antoine',     'Morel',       645673846180, 0, TRUE,  'PROTO09'),
    (36, 'test@test.com', TRUE, 'Marine',      'Lambert',     655383876950, 1, TRUE,  'PROTO10'),
    (37, 'test@test.com', TRUE, 'Alexandre',   'Faure',       665093907720, 0, TRUE,  'PROTO11'),
    (38, 'test@test.com', TRUE, 'Elodie',      'Renault',     674803938490, 1, TRUE,  'PROTO12'),
    (39, 'test@test.com', TRUE, 'Maxime',      'Petit',       684513969260, 0, TRUE,  'PROTO13'),
    (40, 'test@test.com', TRUE, 'Amélie',      'Gautier',     694224000030, 1, TRUE,  'PROTO14'),
    (41, 'test@test.com', TRUE, 'Damien',      'Roux',        703934030800, 0, TRUE,  'PROTO15'),
    (42, 'test@test.com', TRUE, 'Claire',      'Dubois',      713644061570, 1, TRUE,  'PROTO16'),
    (43, 'test@test.com', TRUE, 'Hugo',        'Simon',       723354092340, 0, TRUE,  'PROTO17'),
    (44, 'test@test.com', TRUE, 'Chloé',       'Lemoine',     733064123110, 1, TRUE,  'PROTO18'),
    (45, 'test@test.com', TRUE, 'Romain',      'Bertrand',    742774153880, 0, TRUE,  'PROTO19'),
    (46, 'test@test.com', TRUE, 'Alice',       'Renault',     752484184650, 1, TRUE,  'PROTO20'),
    (47, 'test@test.com', TRUE, 'Guillaume',   'Faure',       762194215420, 0, TRUE,  'PROTO21'),
    (48, 'test@test.com', TRUE, 'Sébastien',   'Dupuis',      771904246190, 0, TRUE,  'SABIANE01'),
    (49, 'test@test.com', TRUE, 'Marine',      'Lefevre',     781614276960, 0, TRUE,  'SABIANE02'),
    (50, 'test@test.com', TRUE, 'Thierry',     'Bernard',     791324307730, 0, TRUE,  'SABIANE03'),
    (51, 'test@test.com', TRUE, 'Sylvie',      'Martin',      801034338500, 0, TRUE,  'SABIANE04'),
    (52, 'test@test.com', TRUE, 'Laurence',    'Girard',      810744369270, 0, TRUE,  'SABIANE05'),
    (53, 'test@test.com', TRUE, 'Dominique',   'Moreau',      820454400040, 0, TRUE,  'SABIANE06'),
    (54, 'test@test.com', TRUE, 'Juliette',    'Roussel',     830164430810, 0, TRUE,  'SABIANE07'),
    (55, 'test@test.com', TRUE, 'Pascal',      'Dubois',      839874461580, 0, TRUE,  'SABIANE08'),
    (56, 'test@test.com', TRUE, 'Amandine',    'Perrin',      849584492350, 0, TRUE,  'SABIANE09'),
    (57, 'test@test.com', TRUE, 'Nicolas',     'Lambert',     859294523120, 0, TRUE,  'SABIANE10'),
    (58, 'test@test.com', TRUE, 'Bruno',       'Simon',       869004553890, 0, TRUE,  'SABIANE11'),
    (59, 'test@test.com', TRUE, 'Véronique',   'Petit',       878714584660, 0, TRUE,  'SABIANE12'),
    (60, 'test@test.com', TRUE, 'Philippe',    'Garnier',     888424615430, 0, TRUE,  'SABIANE13'),
    (61, 'test@test.com', TRUE, 'Catherine',   'Renault',     898134646200, 0, TRUE,  'SABIANE14'),
    (62, 'test@test.com', TRUE, 'Jacques',     'Faure',       907844676970, 0, TRUE,  'SABIANE15'),
    (63, 'test@test.com', TRUE, 'Marion',      'Cotille',     917554707740, 1, FALSE, 'PROTO01'),
    (64, 'test@test.com', TRUE, 'Arthur',      'Couyer',      927264738510, 1, FALSE, 'PROTO03'),
    (65, 'test@test.com', TRUE, 'Kaa',         'Melott',      936974769280, 0, FALSE, 'PROTO05'),
    (66, 'test@test.com', TRUE, 'Père',        'Seval',       946684800050, 0, FALSE, 'PROTO06');


INSERT INTO public.phone_number (id, favorite, number, source, person_id) VALUES
    (1,  TRUE, '+33677542802', 0, 1),
    (2,  FALSE,'+33677542803', 0, 1),
    (3,  TRUE, '+33677542804', 0, 2),
    (4,  TRUE, '+33677542805', 0, 3),
    (5,  TRUE, '+33677542806', 0, 4),
    (6,  TRUE, '+33677542807', 0, 5),
    (7,  TRUE, '+33677542808', 0, 6),
    (8,  TRUE, '+33677542809', 0, 7),
    (9,  TRUE, '+33677542810', 0, 8),
    (10, TRUE, '+33677542811', 0, 9),
    (11, TRUE, '+33677542812', 0, 10),
    (12, TRUE, '+33677542813', 0, 11),
    (13, TRUE, '+33677542814', 0, 12),
    (14, TRUE, '+33677542815', 0, 13),
    (15, TRUE, '+33677542816', 0, 14),
    (16, FALSE,'+33677542817', 0, 15),
    (17, TRUE, '+33677542818', 0, 16),
    (18, TRUE, '+33677542819', 0, 17),
    (19, TRUE, '+33677542820', 0, 18),
    (20, TRUE, '+33677542821', 0, 19),
    (21, TRUE, '+33677542822', 0, 20),
    (22, TRUE, '+33677542823', 0, 21),
    (23, TRUE, '+33677542824', 0, 22),
    (24, TRUE, '+33677542825', 0, 23),
    (25, TRUE, '+33677542826', 0, 24),
    (26, TRUE, '+33677542827', 0, 25),
    (27, TRUE, '+33677542828', 0, 26),
    (28, TRUE, '+33677542829', 0, 27),
    (29, TRUE, '+33677542830', 0, 28),
    (30, TRUE, '+33677542831', 0, 29),
    (31, TRUE, '+33677542832', 0, 30),
    (32, TRUE, '+33677542833', 0, 31),
    (33, TRUE, '+33677542834', 0, 32),
    (34, TRUE, '+33677542835', 0, 33),
    (35, TRUE, '+33677542836', 0, 34),
    (36, TRUE, '+33677542837', 0, 35),
    (37, TRUE, '+33677542838', 0, 36),
    (38, TRUE, '+33677542839', 0, 37),
    (39, TRUE, '+33677542840', 0, 38),
    (40, TRUE, '+33677542841', 0, 39),
    (41, TRUE, '+33677542842', 0, 40),
    (42, TRUE, '+33677542843', 0, 41),
    (43, TRUE, '+33677542844', 0, 42),
    (44, TRUE, '+33677542845', 0, 43),
    (45, TRUE, '+33677542846', 0, 44),
    (46, TRUE, '+33677542847', 0, 45),
    (47, TRUE, '+33677542848', 0, 46),
    (48, TRUE, '+33677542849', 0, 47),
    (49, TRUE, '+33677542850', 0, 48),
    (50, TRUE, '+33677542851', 0, 49),
    (51, TRUE, '+33677542852', 0, 50),
    (52, TRUE, '+33677542853', 0, 51),
    (53, TRUE, '+33677542854', 0, 52),
    (54, TRUE, '+33677542855', 0, 53),
    (55, TRUE, '+33677542856', 0, 54),
    (56, TRUE, '+33677542857', 0, 55),
    (57, TRUE, '+33677542858', 0, 56),
    (58, TRUE, '+33677542859', 0, 57),
    (59, TRUE, '+33677542860', 0, 58),
    (60, TRUE, '+33677542861', 0, 59),
    (61, TRUE, '+33677542862', 0, 60),
    (62, TRUE, '+33677542863', 0, 61),
    (63, TRUE, '+33677542864', 0, 62),
    (64, TRUE, '+33677542865', 0, 63),
    (65, TRUE, '+33677542866', 0, 64),
    (66, TRUE, '+33677542867', 0, 65),
    (67, TRUE, '+33677542868', 0, 66);


INSERT INTO public.state (id, date, type, survey_unit_id) VALUES
    (1, 111112111,'VIN', '11'),
    (2, 110111111,'NNS', '11'),
    (3, 111111111,'TBR', '12'),
    (4, 111111111,'TBR', '13'),
    (5, 111111111,'TBR', '14'),
    (6, 101111111,'TBR', '11'),
    (7, 101111111,'TBR', '24'),
    (8, 1590504478334, 'VIC', '20'),
    (9, 1590504478334, 'VIC', '21'),
    (10, 1590504478334, 'FIN', '22'),
    (11, 1590504478334, 'VIC', '23'),
    (12, 111112111,'VIN', '25'),
    (13, 110111111,'NNS', '25'),
    (14, 111111111,'TBR', '26'),
    (15, 111111111,'TBR', '27'),
    (16, 111111111,'TBR', '28'),
    (17, 101111111,'TBR', '25'),
    (18, 101111111,'TBR', '33'),
    (19, 1590504478334, 'VIC', '29'),
    (20, 1590504478334, 'VIC', '30'),
    (21, 1590504478334, 'FIN', '31'),
    (22, 1590504478334, 'VIC', '32');

INSERT INTO public.contact_outcome (id, date, type, survey_unit_id) VALUES
    (1, 1590504478334, 'DUK', '24'),
    (2, 1590504478334, 'DUK', '33');

INSERT INTO public.comment (id, type, value, survey_unit_id) VALUES
    (1, 'INTERVIEWER', 'un commentaire', '13'),
    (2, 'INTERVIEWER', 'un commentaire', '27');

INSERT INTO closing_cause (id, date, type, survey_unit_id) VALUES
    (1, EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000, 'NPI', '11'),
    (2, EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000, 'NPI', '25');


INSERT INTO public.identification (id, survey_unit_id, identification_type, identification,access,situation,category,occupant) VALUES
    (1, '11', 'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED'),
    (2, '21', 'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED'),
    (3, '25', 'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED'),
    (4, '30', 'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED');

INSERT INTO public.communication_template (id, meshuggah_id, medium, type, campaign_id) VALUES
    (1, 'mesh1', 'EMAIL', 'REMINDER', 'SIMPSONS2020X00'),
    (2, 'mesh2', 'LETTER', 'NOTICE', 'SIMPSONS2020X00'),
    (3, 'mesh3', 'EMAIL', 'REMINDER', 'VQS2021X00'),
    (4, 'mesh4', 'LETTER', 'NOTICE', 'VQS2021X00'),
    (5, 'mesh5', 'EMAIL', 'NOTICE', 'VQS2021X00');

INSERT INTO public.communication_request (survey_unit_id, emitter, reason, campaign_id, meshuggah_id) VALUES
    (1, '11', 'INTERVIEWER', 'REFUSAL', 'SIMPSONS2020X00', 'mesh1'),
    (2, '11', 'INTERVIEWER', 'UNREACHABLE', 'SIMPSONS2020X00', 'mesh2'),
    (3, '20', 'INTERVIEWER', 'REFUSAL', 'VQS2021X00', 'mesh3'),
    (4, '20', 'INTERVIEWER', 'UNREACHABLE', 'VQS2021X00', 'mesh4');

INSERT INTO public.communication_request_status (communication_request_id, status, date) VALUES
    (1, 'INITIATED', 1721903754305),
    (1, 'READY', 1721903755305),
    (1, 'SUBMITTED', 1721903756305),
    (2, 'INITIATED', 1721903754305),
    (2, 'READY', 1721903756310),
    (3, 'INITIATED', 1721903754205),
    (4, 'INITIATED', 1721903754205);

INSERT INTO public.communication_metadata (survey_unit_id, campaign_id, meshuggah_id, metadata_key, metadata_value) VALUES
    ('11','SIMPSONS2020X00','mesh1','recipient_full_name', 'Albert Einstein'),
    ('11', 'SIMPSONS2020X00','mesh1','recipient_address', '112 Mercer Street, Princeton, New Jersey');

SELECT setval(
    pg_get_serial_sequence('public.communication_request_status', 'id'),
    COALESCE((SELECT MAX(id) FROM public.communication_request_status), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.communication_request', 'id'),
    COALESCE((SELECT MAX(id) FROM public.communication_request), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.communication_template', 'id'),
    COALESCE((SELECT MAX(id) FROM public.communication_template), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.communication_metadata', 'id'),
    COALESCE((SELECT MAX(id) FROM public.communication_metadata), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.contact_attempt', 'id'),
    COALESCE((SELECT MAX(id) FROM public.contact_attempt), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.referent', 'id'),
    COALESCE((SELECT MAX(id) FROM public.referent), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.message', 'id'),
    COALESCE((SELECT MAX(id) FROM public.message), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.sample_identifier', 'id'),
    COALESCE((SELECT MAX(id) FROM public.sample_identifier), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.identification', 'id'),
    COALESCE((SELECT MAX(id) FROM public.identification), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.person', 'id'),
    COALESCE((SELECT MAX(id) FROM public.person), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.phone_number', 'id'),
    COALESCE((SELECT MAX(id) FROM public.phone_number), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.state', 'id'),
    COALESCE((SELECT MAX(id) FROM public.state), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.contact_outcome', 'id'),
    COALESCE((SELECT MAX(id) FROM public.contact_outcome), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.comment', 'id'),
    COALESCE((SELECT MAX(id) FROM public.comment), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.closing_cause', 'id'),
    COALESCE((SELECT MAX(id) FROM public.closing_cause), 0) + 1,
    false
);

SELECT setval(
    pg_get_serial_sequence('public.address', 'id'),
    COALESCE((SELECT MAX(id) FROM public.address), 0) + 1,
    false
);