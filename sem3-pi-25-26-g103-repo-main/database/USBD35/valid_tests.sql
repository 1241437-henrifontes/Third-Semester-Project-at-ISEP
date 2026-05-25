DECLARE
    p_facility_id1 FACILITY.FACILITYID%TYPE := 53;
    p_facility_name1 FACILITY.NAME%TYPE := 'MyFacility05';
    p_facility_id2 FACILITY.FACILITYID%TYPE := 54;
    p_facility_name2 FACILITY.NAME%TYPE := 'MyFacility06';
    p_facility_id3 FACILITY.FACILITYID%TYPE := 55;
    p_facility_name3 FACILITY.NAME%TYPE := 'MyFacility07';
    p_facility_id4 FACILITY.FACILITYID%TYPE := 56;
    p_facility_name4 FACILITY.NAME%TYPE := 'MyFacility08';
BEGIN
    registerNewFacility(p_facilityId => p_facility_id1, p_facilityName => p_facility_name1);
    registerNewFacility(p_facilityId => p_facility_id2, p_facilityName => p_facility_name2);
    registerNewFacility(p_facilityId => p_facility_id3, p_facilityName => p_facility_name3);
    registerNewFacility(p_facilityId => p_facility_id4, p_facilityName => p_facility_name4);
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
