BEGIN;

    ALTER TABLE communication_template
    DROP COLUMN id;

COMMIT;
