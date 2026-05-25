DECLARE
    c_cur      SYS_REFCURSOR;
    v_wagonId  Wagon.wagonId%TYPE;
BEGIN
    c_cur := GetUnusedWagonsCursor (
            p_start_date => DATE '2025-01-01',
            p_end_date   => DATE '2025-11-01'
    );

    LOOP
        FETCH c_cur INTO v_wagonId;
        EXIT WHEN c_cur%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Not used wagon: ' || v_wagonId);
    END LOOP;

    CLOSE c_cur;
END;
/
