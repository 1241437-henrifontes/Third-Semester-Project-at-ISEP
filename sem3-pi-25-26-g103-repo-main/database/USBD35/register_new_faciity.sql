CREATE OR REPLACE PROCEDURE registerNewFacility (
    p_facilityId IN FACILITY.FACILITYID%TYPE,
    p_facilityName IN FACILITY.NAME%TYPE
)
IS
    e_null_id EXCEPTION;
    e_neg_zero_id EXCEPTION;
    e_null_name EXCEPTION;
BEGIN
    IF p_facilityId IS NULL THEN
        RAISE e_null_id;
    END IF;

    IF p_facilityName IS NULL THEN
        RAISE e_null_name;
    END IF;

    IF p_facilityId <= 0 THEN
        RAISE e_neg_zero_id;
    END IF;

    INSERT INTO FACILITY (facilityId, name) VALUES (p_facilityId, p_facilityName);

EXCEPTION
    WHEN e_null_id THEN
        RAISE_APPLICATION_ERROR(-20001, 'Facility ID cannot be null.');
    WHEN e_null_name THEN
        RAISE_APPLICATION_ERROR(-20002, 'Facility name cannot be null.');
    WHEN e_neg_zero_id THEN
        RAISE_APPLICATION_ERROR(-20003, 'Facility ID cannot be negative or zero.');
    WHEN DUP_VAL_ON_INDEX THEN
        RAISE_APPLICATION_ERROR(-20004, 'Facility with id ' || p_facilityId || ' already exists.');
    WHEN OTHERS THEN
        RAISE;
END;
/
