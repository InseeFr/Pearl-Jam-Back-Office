BEGIN;

-- 1. Add new columns to communication_request
ALTER TABLE communication_request ADD COLUMN campaign_id VARCHAR(255);
ALTER TABLE communication_request ADD COLUMN meshuggah_id VARCHAR(255);

-- 2. Update the new columns with existing values
UPDATE communication_request
SET campaign_id = (SELECT campaign_id FROM communication_template WHERE communication_template.id = communication_request.communication_template_id),
    meshuggah_id = (SELECT meshuggah_id FROM communication_template WHERE communication_template.id = communication_request.communication_template_id)
WHERE communication_template_id IS NOT NULL;

-- 3. Add the NOT NULL constraint now that values are populated
ALTER TABLE communication_request ALTER COLUMN campaign_id SET NOT NULL;
ALTER TABLE communication_request ALTER COLUMN meshuggah_id SET NOT NULL;

-- 4. Remove the existing foreign key on communication_template_id
ALTER TABLE communication_request DROP CONSTRAINT IF EXISTS fk_comm_request_to_comm_request_template;

-- 5. Add an index to optimize joins
CREATE INDEX idx_comm_request_campaign_meshuggah ON communication_request(campaign_id, meshuggah_id);

-- 6. Drop the old column communication_template_id from communication_request
ALTER TABLE communication_request DROP COLUMN communication_template_id;

-- 7. Remove communication_template_id from communication_metadata
ALTER TABLE communication_metadata DROP COLUMN communication_template_id;

-- 8. Add new columns to communication_metadata
ALTER TABLE communication_metadata ADD COLUMN campaign_id VARCHAR(255) NOT NULL;
ALTER TABLE communication_metadata ADD COLUMN meshuggah_id VARCHAR(255) NOT NULL;

-- 9. Add an index on communication_metadata
CREATE INDEX idx_comm_metadata_campaign_meshuggah ON communication_metadata(campaign_id, meshuggah_id);

-- 10. Add foreign key constraints to ensure data integrity

-- Drop the old primary key on id in communication_template
ALTER TABLE communication_template DROP CONSTRAINT communication_templatePK;

-- Set a new primary key using campaign_id and meshuggah_id
ALTER TABLE communication_template
    ADD CONSTRAINT pk_communication_template PRIMARY KEY (campaign_id, meshuggah_id);

-- Add foreign key constraints to communication_request and communication_metadata
-- to enforce consistency with communication_template and maintain referential integrity
ALTER TABLE communication_request
    ADD CONSTRAINT fk_comm_request_campaign FOREIGN KEY (campaign_id, meshuggah_id)
        REFERENCES communication_template (campaign_id, meshuggah_id)
        ON DELETE CASCADE;

ALTER TABLE communication_metadata
    ADD CONSTRAINT fk_comm_metadata_campaign FOREIGN KEY (campaign_id, meshuggah_id)
        REFERENCES communication_template (campaign_id, meshuggah_id)
        ON DELETE CASCADE;

-- 11. Drop the id column from communication_template
ALTER TABLE communication_template DROP COLUMN id;

COMMIT;
