UPDATE identification
SET identification_state =
    CASE
        WHEN identification IS NULL THEN 'MISSING'
        WHEN identification IN ('DESTROY', 'UNIDENTIFIED') THEN 'FINISHED'
        WHEN access IS NULL THEN 'ONGOING'
        WHEN situation IS NULL THEN 'ONGOING'
        WHEN situation IN ('ABSORBED', 'NOORDINARY') THEN 'FINISHED'
        WHEN category IS NULL THEN 'ONGOING'
        WHEN category IN ('VACANT', 'SECONDARY') THEN 'FINISHED'
        WHEN occupant IS NULL THEN 'ONGOING'
        ELSE 'FINISHED'
    END;