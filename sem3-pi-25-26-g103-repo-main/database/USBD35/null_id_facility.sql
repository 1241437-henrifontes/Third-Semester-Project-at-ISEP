DECLARE
    p_facility_id FACILITY.FACILITYID%TYPE := NULL;
    p_facility_name FACILITY.NAME%TYPE := 'MyFacility03';
BEGIN
    registerNewFacility(p_facilityId => p_facility_id, p_facilityName => p_facility_name);
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
