DECLARE
    p_facility_id1 FACILITY.FACILITYID%TYPE := 51;
    p_facility_name1 FACILITY.NAME%TYPE := 'MyFacility01';
    p_facility_id2 FACILITY.FACILITYID%TYPE := 51;
    p_facility_name2 FACILITY.NAME%TYPE := 'MyFacility02';
BEGIN
    registerNewFacility(p_facilityId => p_facility_id1, p_facilityName => p_facility_name1);
    registerNewFacility(p_facilityId => p_facility_id2, p_facilityName => p_facility_name2);
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
