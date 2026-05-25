CREATE OR REPLACE TRIGGER trg_check_train_length
    BEFORE INSERT OR UPDATE ON Freight_Wagon
    FOR EACH ROW
DECLARE
    v_total_length NUMBER := 0;
    v_max_length NUMBER;
    v_train_id NUMBER;
    v_new_wagon_length NUMBER;
BEGIN
    SELECT f.TrainId
    INTO v_train_id
    FROM Freight f
    WHERE f.id = :NEW.FreightId;

    SELECT t.maxLength
    INTO v_max_length
    FROM Train t
    WHERE t.id = v_train_id;

    IF v_max_length <= 0 THEN
        RAISE_APPLICATION_ERROR(
                -20201,
                'Train maximum length is invalid (must be > 0).'
        );
    END IF;

    IF UPDATING THEN
        SELECT NVL(SUM(d.length), 0)
        INTO v_total_length
        FROM Freight_Wagon fw
                 INNER JOIN Freight f ON f.id = fw.FreightId
                 INNER JOIN Wagon w ON w.wagonId = fw.wagonId
                 INNER JOIN RollingStock rs ON rs.id = w.RollingStockId
                 INNER JOIN RollingStockModel rsm ON rsm.id = rs.RollingStockModelId
                 INNER JOIN RollingStockDimension d ON d.id = rsm.RollingStockDimensionId
        WHERE f.TrainId = v_train_id
          AND NOT (fw.FreightId = :OLD.FreightId AND fw.wagonId = :OLD.wagonId);
    ELSE
        SELECT NVL(SUM(d.length), 0)
        INTO v_total_length
        FROM Freight_Wagon fw
                 INNER JOIN Freight f ON f.id = fw.FreightId
                 INNER JOIN Wagon w ON w.wagonId = fw.wagonId
                 INNER JOIN RollingStock rs ON rs.id = w.RollingStockId
                 INNER JOIN RollingStockModel rsm ON rsm.id = rs.RollingStockModelId
                 INNER JOIN RollingStockDimension d ON d.id = rsm.RollingStockDimensionId
        WHERE f.TrainId = v_train_id;
    END IF;

    SELECT d.length
    INTO v_new_wagon_length
    FROM Wagon w
             INNER JOIN RollingStock rs ON rs.id = w.RollingStockId
             INNER JOIN RollingStockModel rsm ON rsm.id = rs.RollingStockModelId
             INNER JOIN RollingStockDimension d ON d.id = rsm.RollingStockDimensionId
    WHERE w.wagonId = :NEW.wagonId;

    IF v_total_length + v_new_wagon_length > v_max_length THEN
        RAISE_APPLICATION_ERROR(
                -20200,
                'Train maximum length exceeded: ' ||
                (v_total_length + v_new_wagon_length) || ' m > ' || v_max_length || ' m'
        );
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(
                -20998,
                'Required data not found. Check if Freight, Train, or Wagon exists.'
        );
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(
                -20999,
                'Unexpected error: ' || SQLERRM
        );
END;
/