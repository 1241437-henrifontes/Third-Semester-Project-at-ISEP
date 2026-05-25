CREATE OR REPLACE PROCEDURE prc_add_building_to_facility(
    p_facility_id IN Facility.facilityId%TYPE,
    p_building_id IN Building.id%TYPE
) AS
    v_count NUMBER;
BEGIN
    SELECT COUNT(*) INTO v_count FROM Facility WHERE facilityId = p_facility_id;
    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20010, 'Facility with ID ' || p_facility_id || ' does not exist.');
    END IF;

    SELECT COUNT(*) INTO v_count FROM Building WHERE id = p_building_id;
    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20011, 'Building with ID ' || p_building_id || ' does not exist.');
    end if;

    SELECT COUNT(*) INTO v_count
    FROM Facility_Building 
    WHERE facilityID = p_facility_id AND buildingID = p_building_id;

    IF v_count = 0 THEN
        INSERT INTO Facility_Building (facilityID, buildingID)
        VALUES (p_facility_id, p_building_id);
        DBMS_OUTPUT.PUT_LINE('Building ' || p_building_id || ' associated with facility ' || p_facility_id || '.');
    ELSE
        RAISE_APPLICATION_ERROR(-20012, 'Building ' || p_building_id || ' is already associated with facility ' || p_facility_id || '.');
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE > -20010 AND SQLCODE < -20012 THEN
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        END IF;
        ROLLBACK;
        RAISE;
END;
/
