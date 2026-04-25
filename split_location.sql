BEGIN TRANSACTION;
ALTER TABLE Resource ADD COLUMN building TEXT;
ALTER TABLE Resource ADD COLUMN room TEXT;
UPDATE Resource SET building = CASE WHEN INSTR(location, ',') > 0 THEN TRIM(SUBSTR(location, 1, INSTR(location, ',') - 1)) ELSE location END, room = CASE WHEN INSTR(location, ',') > 0 THEN TRIM(SUBSTR(location, INSTR(location, ',') + 1)) ELSE '' END;
ALTER TABLE Resource DROP COLUMN location;
COMMIT;
