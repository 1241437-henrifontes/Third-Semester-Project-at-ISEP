DECLARE
    p_facility_id FACILITY.FACILITYID%TYPE := 1;
    p_facility_name FACILITY.NAME%TYPE := 'MyFacility04';
BEGIN
    registerNewFacility(p_facilityId => p_facility_id, p_facilityName => p_facility_name);
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
