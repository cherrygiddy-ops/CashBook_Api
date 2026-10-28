ALTER TABLE cashbook_members
DROP CONSTRAINT IF EXISTS chk_cashbook_member_role;

ALTER TABLE cashbook_members
    ADD CONSTRAINT chk_cashbook_member_role
        CHECK (member_role IN ('EDITOR', 'VIEWER'));