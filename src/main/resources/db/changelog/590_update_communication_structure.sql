BEGIN;

-- Ajouter les colonnes campaign_id et meshuggah_id dans communication_request
ALTER TABLE communication_request
    ADD COLUMN campaign_id VARCHAR(255),
    ADD COLUMN meshuggah_id VARCHAR(255);

-- Mettre à jour les nouvelles colonnes avec les valeurs existantes
UPDATE communication_request cr
SET campaign_id = ct.campaign_id,
    meshuggah_id = ct.meshuggah_id
FROM communication_template ct
WHERE cr.communication_template_id = ct.id;

-- Ajouter la contrainte NOT NULL maintenant que les colonnes ne contiennent plus de NULL
ALTER TABLE communication_request
    ALTER COLUMN campaign_id SET NOT NULL,
    ALTER COLUMN meshuggah_id SET NOT NULL;

-- Vérifier qu'il n'y a pas de doublons avant de modifier la clé primaire
SELECT campaign_id, meshuggah_id, COUNT(*)
FROM communication_request
GROUP BY campaign_id, meshuggah_id
HAVING COUNT(*) > 1;

-- Supprimer la contrainte de clé primaire existante (communication_requestPK)
ALTER TABLE communication_request DROP CONSTRAINT IF EXISTS communication_requestPK;

-- Ajouter la nouvelle clé primaire sur campaign_id et meshuggah_id
ALTER TABLE communication_request ADD PRIMARY KEY (campaign_id, meshuggah_id);

-- Modifier la clé primaire de communication_template
ALTER TABLE communication_template DROP CONSTRAINT IF EXISTS communication_templatePK;
ALTER TABLE communication_template ADD PRIMARY KEY (campaign_id, meshuggah_id);

-- Modifier la clé étrangère dans communication_request
-- Vérifier que la contrainte FK506gklsgdfiner7hc3vbo77ku existe avant de la supprimer
ALTER TABLE communication_request DROP CONSTRAINT IF EXISTS FK506gklsgdfiner7hc3vbo77ku;

-- Ajouter la nouvelle contrainte de clé étrangère
ALTER TABLE communication_request
    ADD CONSTRAINT fk_comm_request_to_comm_request_template FOREIGN KEY (campaign_id, meshuggah_id)
        REFERENCES communication_template (campaign_id, meshuggah_id);

-- Supprimer la colonne communication_template_id dans communication_metadata
ALTER TABLE communication_metadata DROP COLUMN IF EXISTS communication_template_id;

-- Ajouter les nouvelles colonnes campaign_id et meshuggah_id dans communication_metadata
ALTER TABLE communication_metadata
    ADD COLUMN campaign_id VARCHAR(255) NOT NULL,
    ADD COLUMN meshuggah_id VARCHAR(255) NOT NULL;

-- Supprimer la colonne id dans communication_template
-- Vérifier que communication_template a bien une nouvelle clé primaire avant de supprimer la colonne id
ALTER TABLE communication_template DROP COLUMN IF EXISTS id;

COMMIT;
