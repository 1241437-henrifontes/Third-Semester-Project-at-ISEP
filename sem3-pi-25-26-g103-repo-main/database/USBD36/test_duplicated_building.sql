DECLARE
    v_facility_id Facility.facilityId%TYPE := 5;
    v_building_id Building.id%TYPE := 1;
BEGIN
prc_add_building_to_facility(v_facility_id, v_building_id);
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -20012 THEN
            DBMS_OUTPUT.PUT_LINE('Caught expected error: ' || SQLERRM);
ELSE
            DBMS_OUTPUT.PUT_LINE('Caught unexpected error: ' || SQLERRM);
END IF;
END;
/
