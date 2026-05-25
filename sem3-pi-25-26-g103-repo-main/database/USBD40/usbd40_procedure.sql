CREATE OR REPLACE TYPE wagon_id_list AS TABLE OF VARCHAR2(9);
/

CREATE OR REPLACE PROCEDURE prc_register_freight(
    p_freight_id IN Freight.id%TYPE,
    p_train_id IN Freight.TrainId%TYPE,
    p_origin IN Freight.origin%TYPE,
    p_destination IN Freight.destination%TYPE,
    p_wagon_ids IN wagon_id_list
) AS
    v_count NUMBER;
    v_wagon_type_id WagonType.typeId%TYPE;
    v_current_location NUMBER;
    v_wagon_id VARCHAR2(9);
    v_total_length NUMBER := 0;
    v_max_train_length NUMBER;
    v_item_length NUMBER;
BEGIN
    IF p_freight_id IS NULL OR p_freight_id < 0 THEN
        RAISE_APPLICATION_ERROR(-20000, 'Freight id cannot be negative or null.');
    END IF;
    BEGIN
        SELECT maxLength INTO v_max_train_length FROM Train WHERE id = p_train_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20001, 'Train does not exists.');
    END;

    SELECT COUNT(*) INTO v_count FROM Facility WHERE facilityId = p_origin;
    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Origin facility does not exists.');
    END IF;

    SELECT COUNT(*) INTO v_count FROM Facility WHERE facilityId = p_destination;
    IF v_count = 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Destination facility does not exists.');
    END IF;

    IF p_wagon_ids IS EMPTY THEN
        RAISE_APPLICATION_ERROR(-20004, 'Wagons list cannot be empty.');
    END IF;

    FOR i IN 1..p_wagon_ids.COUNT LOOP
        v_wagon_id := p_wagon_ids(i);

        BEGIN
            SELECT wm.WagonTypeId, rsd.length INTO v_wagon_type_id, v_item_length
            FROM Wagon w
            JOIN RollingStock rs ON w.RollingStockId = rs.id
            JOIN RollingStockModel rsm ON rs.RollingStockModelId = rsm.id
            JOIN RollingStockDimension rsd ON rsm.RollingStockDimensionId = rsd.id
            JOIN WagonModel wm ON rsm.id = wm.RollingStockModelId
            WHERE w.wagonId = v_wagon_id;
            
            v_total_length := v_total_length + v_item_length;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                RAISE_APPLICATION_ERROR(-20005, 'Wagon ' || v_wagon_id || ' does not exists.');
        END;

        FOR f IN 1..p_wagon_ids.COUNT LOOP
                IF p_wagon_ids(i) = p_wagon_ids(f) AND i <> f THEN
                    RAISE_APPLICATION_ERROR(-20006, 'Wagons list cannot contain duplicates.');
                END IF;
            END LOOP;

        BEGIN
            SELECT destination INTO v_current_location
            FROM (
                SELECT f.destination
                FROM Freight f
                JOIN Freight_Wagon fw ON f.id = fw.FreightId
                WHERE fw.wagonId = v_wagon_id
                ORDER BY f.id DESC
            )
            WHERE ROWNUM = 1;
            
            IF v_current_location <> p_origin THEN
                 RAISE_APPLICATION_ERROR(-20007, 'Wagon ' || v_wagon_id || ' does not in departure facility (is in ' || v_current_location || ').');
            END IF;
            
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL;
        END;

        SELECT COUNT(*) INTO v_count
        FROM Freight_Wagon
        WHERE wagonId = v_wagon_id AND FreightId = p_freight_id;
        
        IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20008, 'Wagon ' || v_wagon_id || ' it is already in this freight.');
        END IF;
    END LOOP;

    SELECT SUM(rsd.length) INTO v_item_length
    FROM Train_Locomotive tl
    JOIN Locomotive l ON tl.LocomotiveId = l.id
    JOIN RollingStock rs ON l.RollingStockId = rs.id
    JOIN RollingStockModel rsm ON rs.RollingStockModelId = rsm.id
    JOIN RollingStockDimension rsd ON rsm.RollingStockDimensionId = rsd.id
    WHERE tl.TrainId = p_train_id;
    
    IF v_item_length IS NOT NULL THEN
        v_total_length := v_total_length + v_item_length;
    END IF;
    
    IF v_total_length > v_max_train_length THEN
        RAISE_APPLICATION_ERROR(-20009, 'The total length (' || v_total_length || ') excede the max length of the train (' || v_max_train_length || ').');
    END IF;

    INSERT INTO Freight (id, TrainId, origin, destination)
    VALUES (p_freight_id, p_train_id, p_origin, p_destination);

    FOR i IN 1..p_wagon_ids.COUNT LOOP
        INSERT INTO Freight_Wagon (FreightId, wagonId)
        VALUES (p_freight_id, p_wagon_ids(i));
    END LOOP;

    DBMS_OUTPUT.PUT_LINE('Freight ' || p_freight_id || ' successfully registered.');

EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        DBMS_OUTPUT.PUT_LINE('Error: Duplicate freight entry.');
        ROLLBACK;
    WHEN OTHERS THEN
        IF (SQLCODE > -20000 AND SQLCODE < -20009) THEN
            DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        END IF;
        ROLLBACK;
        RAISE;
END;
/
