DECLARE
v_cursor SYS_REFCURSOR;
    v_wagonId Wagon.wagonId%TYPE;
BEGIN
    v_cursor := get_grain_wagons_used_in_all_trains(
        TO_DATE('06/10/2025','DD/MM/YYYY'),
        TO_DATE('06/10/2025','DD/MM/YYYY')
    );

    LOOP
FETCH v_cursor INTO v_wagonId;
        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('Wagon used in ALL grain trains: ' || v_wagonId);
END LOOP;

CLOSE v_cursor;
END;
/
