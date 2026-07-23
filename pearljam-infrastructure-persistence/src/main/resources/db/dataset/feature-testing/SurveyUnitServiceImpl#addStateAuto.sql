-- =====================================================================
-- Test data for SurveyUnitServiceImpl#addStateAuto
-- =====================================================================
--
-- addStateAuto() is called from updateStates() only when the survey
-- unit's *current* latest state is WFS. It then:
--
--   1. counts distinct survey units for the same (interviewer_id,
--      campaign_id) that already have contact_outcome.type = 'INA'
--      AND at least one state of type 'TBR' anywhere in their history
--      -> findCountUeINATBRByInterviewerIdAndCampaignId
--   2. if that count < 5 AND the current SU's contact_outcome is INA
--        -> adds a new state TBR, clears closing cause
--   3. otherwise
--        -> adds a new state FIN, clears closing cause
--
-- This script creates two dedicated interviewers and one campaign
-- (ADDSTATEAUTOX00) so the "count < 5" boundary is fully controlled and
-- not polluted by any other seed data.
--
--   INTERV1      -> 0 pre-existing INA+TBR survey units (count = 0)
--   INTERV2  -> 5 pre-existing INA+TBR survey units (count = 5)
--
-- IMPORTANT: findCountUeINATBRByInterviewerIdAndCampaignId is a live
-- COUNT against whatever is already in the DB - it does not care about
-- test execution order. That's why AUTOPAST01..05 and AUTOTBR06 must
-- sit on a DIFFERENT interviewer than AUTOTBR01/AUTOFIN01, otherwise
-- the count for INTERV1 would already be 5 before you even run the
-- first test.
--
-- Scenarios created:
--
--   AUTOFIN01     (INTERV1)      no contact_outcome,  latest state WFS  -> expect FIN
--   AUTOTBR01     (INTERV1)      contact_outcome=INA, latest state WFS  -> expect TBR   (count=0 < 5)
--   AUTOPAST01..05 (INTERV2) contact_outcome=INA, latest state TBR  -> "history" units, exist
--                                                                             only to push INTERV2's
--                                                                             count up to 5
--   AUTOTBR06     (INTERV2)  contact_outcome=INA, latest state WFS  -> expect FIN   (count=5, no longer "first five")
--   AUTONOFIRE01  (INTERV1)      contact_outcome=INA, latest state VIC  -> control: addStateAuto must NOT fire
--                                                                             (updateStates won't call it, currentState != WFS)
--
-- How to use:
--   Run this after the base schema + demo data (or standalone, it only
--   depends on organization_unit 'OU-NORTH' existing - see the guard
--   insert below if you run this in isolation).
--
--   Then, to trigger addStateAuto for e.g. AUTOTBR01, call
--   SurveyUnitService.updateSurveyUnit(userId, "AUTOTBR01", updateDto)
--   with an update DTO whose states() is empty/null (or repeats the
--   existing history) - since the WFS state is already the persisted
--   latest state, updateStates() will read it back as currentState and
--   invoke addStateAuto().
-- =====================================================================

-- Guard: make sure OU-NORTH exists even if this script is run standalone
INSERT INTO public.organization_unit (id, label, type, organization_unit_parent_id)
SELECT 'OU-NORTH', 'North region organizational unit', 'LOCAL', NULL
    WHERE NOT EXISTS (SELECT 1 FROM public.organization_unit WHERE id = 'OU-NORTH');

-- ---------------------------------------------------------------------
-- Interviewer & Campaign dedicated to this test
-- ---------------------------------------------------------------------
INSERT INTO public.interviewer (id, email, first_name, last_name, phone_number) VALUES
                                                                                    ('INTERV1',     'interviewer.auto@insee.fr',     'Auguste', 'AutoTest',     '+33900000000'),
                                                                                    ('INTERV2', 'interviewer.autofull@insee.fr', 'Augusta', 'AutoTestFull', '+33900000001');

INSERT INTO public.campaign (id, label, email, identification_configuration, contact_attempt_configuration, contact_outcome_configuration, sensitivity, collect_next_contacts) VALUES
    ('ADDSTATEAUTOX00', 'Campaign for addStateAuto tests', 'addstateauto@test.com', 'HOUSEF2F', 'F2F', 'F2F', false, false);

INSERT INTO public.visibility (
    organization_unit_id, campaign_id,
    collection_end_date, collection_start_date, end_date,
    identification_phase_start_date, interviewer_start_date, management_start_date,
    use_letter_communication, mail, tel
) VALUES
    ('OU-NORTH', 'ADDSTATEAUTOX00',
     EXTRACT(EPOCH FROM NOW() + INTERVAL '1 month') * 1000,
     EXTRACT(EPOCH FROM NOW() - INTERVAL '1 day') * 1000,
     EXTRACT(EPOCH FROM NOW() + INTERVAL '2 months') * 1000,
     EXTRACT(EPOCH FROM NOW() - INTERVAL '2 days') * 1000,
     EXTRACT(EPOCH FROM NOW() - INTERVAL '3 days') * 1000,
     EXTRACT(EPOCH FROM NOW() - INTERVAL '4 days') * 1000,
     true, 'addstateauto@nooneknows.fr', '0900000000');

-- ---------------------------------------------------------------------
-- Addresses (ids 1000-1008), Sample identifiers (ids 1000-1008)
-- ---------------------------------------------------------------------
INSERT INTO public.address (id, dtype, l1, l2, l3, l4, l5, l6, l7, elevator, building, floor, door, staircase, city_priority_district) VALUES
                                                                                                                                           (1000, 'InseeAddress', 'Auto FinNoOutcome', '', '', '1 rue Auto', '', '75000 Paris', 'France', false, null, NULL, NULL, NULL, false),
                                                                                                                                           (1001, 'InseeAddress', 'Auto TbrFirst',     '', '', '2 rue Auto', '', '75000 Paris', 'France', false, null, NULL, NULL, NULL, false),
                                                                                                                                           (1002, 'InseeAddress', 'Auto Past01',       '', '', '3 rue Auto', '', '75000 Paris', 'France', false, null, NULL, NULL, NULL, false),
                                                                                                                                           (1003, 'InseeAddress', 'Auto Past02',       '', '', '4 rue Auto', '', '75000 Paris', 'France', false, null, NULL, NULL, NULL, false),
                                                                                                                                           (1004, 'InseeAddress', 'Auto Past03',       '', '', '5 rue Auto', '', '75000 Paris', 'France', false, null, NULL, NULL, NULL, false),
                                                                                                                                           (1005, 'InseeAddress', 'Auto Past04',       '', '', '6 rue Auto', '', '75000 Paris', 'France', false, null, NULL, NULL, NULL, false),
                                                                                                                                           (1006, 'InseeAddress', 'Auto Past05',       '', '', '7 rue Auto', '', '75000 Paris', 'France', false, null, NULL, NULL, NULL, false),
                                                                                                                                           (1007, 'InseeAddress', 'Auto TbrSixth',     '', '', '8 rue Auto', '', '75000 Paris', 'France', false, null, NULL, NULL, NULL, false),
                                                                                                                                           (1008, 'InseeAddress', 'Auto NoFire',       '', '', '9 rue Auto', '', '75000 Paris', 'France', false, null, NULL, NULL, NULL, false);

INSERT INTO public.sample_identifier (id, dtype, autre, bs, ec, le, nograp, noi, nole, nolog, numfa, rges, ssech) VALUES
                                                                                                                      (1000, 'InseeSampleIdentifier', '1000', 1000, '1', 1000, '1000', 1000, 1000, 1000, 1000, 1000, 1),
                                                                                                                      (1001, 'InseeSampleIdentifier', '1001', 1001, '1', 1001, '1001', 1001, 1001, 1001, 1001, 1001, 1),
                                                                                                                      (1002, 'InseeSampleIdentifier', '1002', 1002, '1', 1002, '1002', 1002, 1002, 1002, 1002, 1002, 1),
                                                                                                                      (1003, 'InseeSampleIdentifier', '1003', 1003, '1', 1003, '1003', 1003, 1003, 1003, 1003, 1003, 1),
                                                                                                                      (1004, 'InseeSampleIdentifier', '1004', 1004, '1', 1004, '1004', 1004, 1004, 1004, 1004, 1004, 1),
                                                                                                                      (1005, 'InseeSampleIdentifier', '1005', 1005, '1', 1005, '1005', 1005, 1005, 1005, 1005, 1005, 1),
                                                                                                                      (1006, 'InseeSampleIdentifier', '1006', 1006, '1', 1006, '1006', 1006, 1006, 1006, 1006, 1006, 1),
                                                                                                                      (1007, 'InseeSampleIdentifier', '1007', 1007, '1', 1007, '1007', 1007, 1007, 1007, 1007, 1007, 1),
                                                                                                                      (1008, 'InseeSampleIdentifier', '1008', 1008, '1', 1008, '1008', 1008, 1008, 1008, 1008, 1008, 1);

-- ---------------------------------------------------------------------
-- Survey units - all attached to INTERV1 / ADDSTATEAUTOX00 / OU-NORTH
-- ---------------------------------------------------------------------
-- NOTE interviewer assignment:
--   INTERV1      -> AUTOFIN01, AUTOTBR01, AUTONOFIRE01   (count = 0)
--   INTERV2  -> AUTOPAST01..05, AUTOTBR06            (count = 5)
INSERT INTO public.survey_unit (id, display_name, priority, address_id, campaign_id, interviewer_id, sample_identifier_id, organization_unit_id) VALUES
                                                                                                                                                     ('AUTOFIN01',    'business-id-autofin01',    FALSE, 1000, 'ADDSTATEAUTOX00', 'INTERV1',     1000, 'OU-NORTH'),
                                                                                                                                                     ('AUTOTBR01',    'business-id-autotbr01',    FALSE, 1001, 'ADDSTATEAUTOX00', 'INTERV1',     1001, 'OU-NORTH'),
                                                                                                                                                     ('AUTOPAST01',   'business-id-autopast01',   FALSE, 1002, 'ADDSTATEAUTOX00', 'INTERV2', 1002, 'OU-NORTH'),
                                                                                                                                                     ('AUTOPAST02',   'business-id-autopast02',   FALSE, 1003, 'ADDSTATEAUTOX00', 'INTERV2', 1003, 'OU-NORTH'),
                                                                                                                                                     ('AUTOPAST03',   'business-id-autopast03',   FALSE, 1004, 'ADDSTATEAUTOX00', 'INTERV2', 1004, 'OU-NORTH'),
                                                                                                                                                     ('AUTOPAST04',   'business-id-autopast04',   FALSE, 1005, 'ADDSTATEAUTOX00', 'INTERV2', 1005, 'OU-NORTH'),
                                                                                                                                                     ('AUTOPAST05',   'business-id-autopast05',   FALSE, 1006, 'ADDSTATEAUTOX00', 'INTERV2', 1006, 'OU-NORTH'),
                                                                                                                                                     ('AUTOTBR06',    'business-id-autotbr06',    FALSE, 1007, 'ADDSTATEAUTOX00', 'INTERV2', 1007, 'OU-NORTH'),
                                                                                                                                                     ('AUTONOFIRE01', 'business-id-autonofire01', FALSE, 1008, 'ADDSTATEAUTOX00', 'INTERV1',     1008, 'OU-NORTH');

-- ---------------------------------------------------------------------
-- Persons (minimal, one respondent per SU) + phone numbers
-- ---------------------------------------------------------------------
INSERT INTO public.person (id, email, first_name, last_name, birthdate, title, privileged, survey_unit_id, panel, contact_history_type) VALUES
                                                                                                                                            (1000, 'auto@test.com', 'Auto', 'FinNoOutcome', 500000000000, 0, TRUE, 'AUTOFIN01',    false, NULL),
                                                                                                                                            (1001, 'auto@test.com', 'Auto', 'TbrFirst',     500000000000, 0, TRUE, 'AUTOTBR01',    false, NULL),
                                                                                                                                            (1002, 'auto@test.com', 'Auto', 'Past01',       500000000000, 0, TRUE, 'AUTOPAST01',   false, NULL),
                                                                                                                                            (1003, 'auto@test.com', 'Auto', 'Past02',       500000000000, 0, TRUE, 'AUTOPAST02',   false, NULL),
                                                                                                                                            (1004, 'auto@test.com', 'Auto', 'Past03',       500000000000, 0, TRUE, 'AUTOPAST03',   false, NULL),
                                                                                                                                            (1005, 'auto@test.com', 'Auto', 'Past04',       500000000000, 0, TRUE, 'AUTOPAST04',   false, NULL),
                                                                                                                                            (1006, 'auto@test.com', 'Auto', 'Past05',       500000000000, 0, TRUE, 'AUTOPAST05',   false, NULL),
                                                                                                                                            (1007, 'auto@test.com', 'Auto', 'TbrSixth',     500000000000, 0, TRUE, 'AUTOTBR06',    false, NULL),
                                                                                                                                            (1008, 'auto@test.com', 'Auto', 'NoFire',       500000000000, 0, TRUE, 'AUTONOFIRE01', false, NULL);

INSERT INTO public.phone_number (id, favorite, number, source, person_id) VALUES
                                                                              (1000, TRUE, '+33900000100', 0, 1000),
                                                                              (1001, TRUE, '+33900000101', 0, 1001),
                                                                              (1002, TRUE, '+33900000102', 0, 1002),
                                                                              (1003, TRUE, '+33900000103', 0, 1003),
                                                                              (1004, TRUE, '+33900000104', 0, 1004),
                                                                              (1005, TRUE, '+33900000105', 0, 1005),
                                                                              (1006, TRUE, '+33900000106', 0, 1006),
                                                                              (1007, TRUE, '+33900000107', 0, 1007),
                                                                              (1008, TRUE, '+33900000108', 0, 1008);

-- ---------------------------------------------------------------------
-- Identification (all IDENTIFIED/FINISHED, not the focus of this test)
-- ---------------------------------------------------------------------
INSERT INTO public.identification (id, survey_unit_id, identification_type, identification, access, situation, category, occupant, identification_state) VALUES
                                                                                                                                                             (1000, 'AUTOFIN01',    'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED', 'FINISHED'),
                                                                                                                                                             (1001, 'AUTOTBR01',    'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED', 'FINISHED'),
                                                                                                                                                             (1002, 'AUTOPAST01',   'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED', 'FINISHED'),
                                                                                                                                                             (1003, 'AUTOPAST02',   'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED', 'FINISHED'),
                                                                                                                                                             (1004, 'AUTOPAST03',   'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED', 'FINISHED'),
                                                                                                                                                             (1005, 'AUTOPAST04',   'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED', 'FINISHED'),
                                                                                                                                                             (1006, 'AUTOPAST05',   'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED', 'FINISHED'),
                                                                                                                                                             (1007, 'AUTOTBR06',    'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED', 'FINISHED'),
                                                                                                                                                             (1008, 'AUTONOFIRE01', 'HOUSEF2F', 'IDENTIFIED', 'ACC', 'ORDINARY', 'PRIMARY', 'IDENTIFIED', 'FINISHED');

-- ---------------------------------------------------------------------
-- Contact outcomes
--   AUTOFIN01     : none  (deliberately no row)
--   AUTOTBR01     : INA
--   AUTOPAST01-05 : INA   (needed to satisfy the count query)
--   AUTOTBR06     : INA
--   AUTONOFIRE01  : INA   (control - won't matter, addStateAuto never runs)
-- ---------------------------------------------------------------------
INSERT INTO public.contact_outcome (id, date, total_number_of_contact_attempts, type, survey_unit_id) VALUES
                                                                                                          (1001, 1760000090000, 2, 'INA', 'AUTOTBR01'),
                                                                                                          (1002, 1760000090000, 2, 'INA', 'AUTOPAST01'),
                                                                                                          (1003, 1760000090000, 2, 'INA', 'AUTOPAST02'),
                                                                                                          (1004, 1760000090000, 2, 'INA', 'AUTOPAST03'),
                                                                                                          (1005, 1760000090000, 2, 'INA', 'AUTOPAST04'),
                                                                                                          (1006, 1760000090000, 2, 'INA', 'AUTOPAST05'),
                                                                                                          (1007, 1760000090000, 2, 'INA', 'AUTOTBR06'),
                                                                                                          (1008, 1760000090000, 2, 'INA', 'AUTONOFIRE01');

-- ---------------------------------------------------------------------
-- States
-- ---------------------------------------------------------------------

-- AUTOFIN01 : NVM -> ANV -> VIN -> VIC -> PRC -> INS -> WFT -> WFS
-- latest = WFS, no contact_outcome -> addStateAuto should add FIN
INSERT INTO public.state (id, date, type, survey_unit_id) VALUES
                                                              (100000, 1760000000000, 'NVM', 'AUTOFIN01'),
                                                              (100001, 1760000012000, 'ANV', 'AUTOFIN01'),
                                                              (100002, 1760000024000, 'VIN', 'AUTOFIN01'),
                                                              (100003, 1760000036000, 'VIC', 'AUTOFIN01'),
                                                              (100004, 1760000048000, 'PRC', 'AUTOFIN01'),
                                                              (100005, 1760000060000, 'INS', 'AUTOFIN01'),
                                                              (100006, 1760000072000, 'WFT', 'AUTOFIN01'),
                                                              (100007, 1760000084000, 'WFS', 'AUTOFIN01');

-- AUTOTBR01 : same progression, contact_outcome=INA, count(INA+TBR)=0 at test time
-- latest = WFS -> addStateAuto should add TBR (0 < 5 and INA)
INSERT INTO public.state (id, date, type, survey_unit_id) VALUES
                                                              (100010, 1760001000000, 'NVM', 'AUTOTBR01'),
                                                              (100011, 1760001012000, 'ANV', 'AUTOTBR01'),
                                                              (100012, 1760001024000, 'VIN', 'AUTOTBR01'),
                                                              (100013, 1760001036000, 'VIC', 'AUTOTBR01'),
                                                              (100014, 1760001048000, 'PRC', 'AUTOTBR01'),
                                                              (100015, 1760001060000, 'INS', 'AUTOTBR01'),
                                                              (100016, 1760001072000, 'WFT', 'AUTOTBR01'),
                                                              (100017, 1760001084000, 'WFS', 'AUTOTBR01');

-- AUTOPAST01..05 : full closed history ending in TBR, contact_outcome=INA
-- These exist purely so findCountUeINATBRByInterviewerIdAndCampaignId
-- returns 5 for INTERV1/ADDSTATEAUTOX00.
INSERT INTO public.state (id, date, type, survey_unit_id) VALUES
                                                              (100020, 1760002000000, 'NVM', 'AUTOPAST01'),
                                                              (100021, 1760002012000, 'ANV', 'AUTOPAST01'),
                                                              (100022, 1760002024000, 'VIN', 'AUTOPAST01'),
                                                              (100023, 1760002036000, 'VIC', 'AUTOPAST01'),
                                                              (100024, 1760002048000, 'PRC', 'AUTOPAST01'),
                                                              (100025, 1760002060000, 'INS', 'AUTOPAST01'),
                                                              (100026, 1760002072000, 'WFT', 'AUTOPAST01'),
                                                              (100027, 1760002084000, 'WFS', 'AUTOPAST01'),
                                                              (100028, 1760002096000, 'FIN', 'AUTOPAST01'),
                                                              (100029, 1760002108000, 'TBR', 'AUTOPAST01'),

                                                              (100030, 1760003000000, 'NVM', 'AUTOPAST02'),
                                                              (100031, 1760003012000, 'ANV', 'AUTOPAST02'),
                                                              (100032, 1760003024000, 'VIN', 'AUTOPAST02'),
                                                              (100033, 1760003036000, 'VIC', 'AUTOPAST02'),
                                                              (100034, 1760003048000, 'PRC', 'AUTOPAST02'),
                                                              (100035, 1760003060000, 'INS', 'AUTOPAST02'),
                                                              (100036, 1760003072000, 'WFT', 'AUTOPAST02'),
                                                              (100037, 1760003084000, 'WFS', 'AUTOPAST02'),
                                                              (100038, 1760003096000, 'FIN', 'AUTOPAST02'),
                                                              (100039, 1760003108000, 'TBR', 'AUTOPAST02'),

                                                              (100040, 1760004000000, 'NVM', 'AUTOPAST03'),
                                                              (100041, 1760004012000, 'ANV', 'AUTOPAST03'),
                                                              (100042, 1760004024000, 'VIN', 'AUTOPAST03'),
                                                              (100043, 1760004036000, 'VIC', 'AUTOPAST03'),
                                                              (100044, 1760004048000, 'PRC', 'AUTOPAST03'),
                                                              (100045, 1760004060000, 'INS', 'AUTOPAST03'),
                                                              (100046, 1760004072000, 'WFT', 'AUTOPAST03'),
                                                              (100047, 1760004084000, 'WFS', 'AUTOPAST03'),
                                                              (100048, 1760004096000, 'FIN', 'AUTOPAST03'),
                                                              (100049, 1760004108000, 'TBR', 'AUTOPAST03'),

                                                              (100050, 1760005000000, 'NVM', 'AUTOPAST04'),
                                                              (100051, 1760005012000, 'ANV', 'AUTOPAST04'),
                                                              (100052, 1760005024000, 'VIN', 'AUTOPAST04'),
                                                              (100053, 1760005036000, 'VIC', 'AUTOPAST04'),
                                                              (100054, 1760005048000, 'PRC', 'AUTOPAST04'),
                                                              (100055, 1760005060000, 'INS', 'AUTOPAST04'),
                                                              (100056, 1760005072000, 'WFT', 'AUTOPAST04'),
                                                              (100057, 1760005084000, 'WFS', 'AUTOPAST04'),
                                                              (100058, 1760005096000, 'FIN', 'AUTOPAST04'),
                                                              (100059, 1760005108000, 'TBR', 'AUTOPAST04'),

                                                              (100060, 1760006000000, 'NVM', 'AUTOPAST05'),
                                                              (100061, 1760006012000, 'ANV', 'AUTOPAST05'),
                                                              (100062, 1760006024000, 'VIN', 'AUTOPAST05'),
                                                              (100063, 1760006036000, 'VIC', 'AUTOPAST05'),
                                                              (100064, 1760006048000, 'PRC', 'AUTOPAST05'),
                                                              (100065, 1760006060000, 'INS', 'AUTOPAST05'),
                                                              (100066, 1760006072000, 'WFT', 'AUTOPAST05'),
                                                              (100067, 1760006084000, 'WFS', 'AUTOPAST05'),
                                                              (100068, 1760006096000, 'FIN', 'AUTOPAST05'),
                                                              (100069, 1760006108000, 'TBR', 'AUTOPAST05');

-- AUTOTBR06 : same progression, contact_outcome=INA, but count is now 5
-- (from the 5 AUTOPAST units above) -> no longer "among first five"
-- latest = WFS -> addStateAuto should add FIN (despite INA outcome)
INSERT INTO public.state (id, date, type, survey_unit_id) VALUES
                                                              (100070, 1760007000000, 'NVM', 'AUTOTBR06'),
                                                              (100071, 1760007012000, 'ANV', 'AUTOTBR06'),
                                                              (100072, 1760007024000, 'VIN', 'AUTOTBR06'),
                                                              (100073, 1760007036000, 'VIC', 'AUTOTBR06'),
                                                              (100074, 1760007048000, 'PRC', 'AUTOTBR06'),
                                                              (100075, 1760007060000, 'INS', 'AUTOTBR06'),
                                                              (100076, 1760007072000, 'WFT', 'AUTOTBR06'),
                                                              (100077, 1760007084000, 'WFS', 'AUTOTBR06');

-- AUTONOFIRE01 : contact_outcome=INA but latest state is VIC, NOT WFS.
-- Control case: updateStates() should never invoke addStateAuto here,
-- since the "currentState == WFS" guard fails.
INSERT INTO public.state (id, date, type, survey_unit_id) VALUES
                                                              (100080, 1760008000000, 'NVM', 'AUTONOFIRE01'),
                                                              (100081, 1760008012000, 'ANV', 'AUTONOFIRE01'),
                                                              (100082, 1760008024000, 'VIN', 'AUTONOFIRE01'),
                                                              (100083, 1760008036000, 'VIC', 'AUTONOFIRE01');

-- ---------------------------------------------------------------------
-- Sequence resets (safe to run even if identity columns weren't touched
-- by explicit ids above, keeps future inserts from colliding)
-- ---------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('public.address',           'id'), COALESCE((SELECT MAX(id) FROM public.address),           0) + 1, false);
SELECT setval(pg_get_serial_sequence('public.sample_identifier', 'id'), COALESCE((SELECT MAX(id) FROM public.sample_identifier), 0) + 1, false);
SELECT setval(pg_get_serial_sequence('public.person',            'id'), COALESCE((SELECT MAX(id) FROM public.person),            0) + 1, false);
SELECT setval(pg_get_serial_sequence('public.phone_number',      'id'), COALESCE((SELECT MAX(id) FROM public.phone_number),      0) + 1, false);
SELECT setval(pg_get_serial_sequence('public.identification',    'id'), COALESCE((SELECT MAX(id) FROM public.identification),    0) + 1, false);
SELECT setval(pg_get_serial_sequence('public.contact_outcome',   'id'), COALESCE((SELECT MAX(id) FROM public.contact_outcome),   0) + 1, false);
SELECT setval(pg_get_serial_sequence('public.state',             'id'), COALESCE((SELECT MAX(id) FROM public.state),             0) + 1, false);