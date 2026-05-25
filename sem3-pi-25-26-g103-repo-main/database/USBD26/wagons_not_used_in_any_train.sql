CREATE OR REPLACE FUNCTION GetUnusedWagonsCursor(
    p_start_date IN DATE,
    p_end_date   IN DATE
) RETURN SYS_REFCURSOR
AS
    c_cursor SYS_REFCURSOR;
    e_null_st_date EXCEPTION;
    e_null_en_date EXCEPTION;
    e_invalid_parameters EXCEPTION;
BEGIN
    IF p_start_date IS NULL THEN
        RAISE e_null_st_date;
    END IF;

    IF p_end_date IS NULL THEN
        RAISE e_null_en_date;
    END IF;

    IF p_start_date > p_end_date THEN
        RAISE e_invalid_parameters;
    END IF;

    OPEN c_cursor FOR
        SELECT w.wagonId
        FROM Wagon w
        WHERE NOT EXISTS (
            SELECT 1
            FROM Freight_Wagon fw
                     INNER JOIN Freight f ON fw.FreightId = f.id
                     INNER JOIN Train   t ON f.TrainId   = t.id
                     INNER JOIN Path    p ON t.id = p.TrainId
            WHERE fw.wagonId = w.wagonId AND p.departureTime BETWEEN p_start_date AND p_end_date)
        ORDER BY w.wagonId;

    RETURN c_cursor;
EXCEPTION
    WHEN e_null_st_date THEN
        RAISE_APPLICATION_ERROR(-20001, 'Start date cannot be null');
    WHEN e_null_en_date THEN
        RAISE_APPLICATION_ERROR(-20002, 'End date cannot be null');
    WHEN e_invalid_parameters THEN
        RAISE_APPLICATION_ERROR(-20003, 'Start date must be before end date');
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20004, 'Unexpected error' || SQLERRM);
END;
/
