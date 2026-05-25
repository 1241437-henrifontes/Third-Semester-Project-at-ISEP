DECLARE
    v_facility_id Facility.facilityId%TYPE := -1;
    v_building_id Building.id%TYPE := 3;
BEGIN
    prc_add_building_to_facility(v_facility_id, v_building_id);
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -20010 THEN
            DBMS_OUTPUT.PUT_LINE('Caught expected error: ' || SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Caught unexpected error: ' || SQLERRM);
        END IF;
END;
/
