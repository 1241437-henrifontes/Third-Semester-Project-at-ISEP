DECLARE
    v_facility_id Facility.facilityId%TYPE := 50;
    v_building_id Building.id%TYPE := 3;
BEGIN
    DELETE FROM FACILITY_BUILDING WHERE facilityId = v_facility_id AND buildingId = v_building_id;

    prc_add_building_to_facility(v_facility_id, v_building_id);

    ROLLBACK;
    DBMS_OUTPUT.PUT_LINE('Test successful.');
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Test failed: ' || SQLERRM);
END;
/
