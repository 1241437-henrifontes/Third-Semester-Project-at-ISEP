-- ============================================================================
-- USLP09 - Train Assembly Functions
-- As Traffic Manager, I want to be able to assemble and assign a train to a route
-- ============================================================================

-- Function to get available locomotives with their current location/status
-- Returns locomotives that are either PARKED or AVAILABLE for train assembly
CREATE OR REPLACE FUNCTION fn_get_available_locomotives_for_route(
    p_route_start_station_id IN NUMBER
) RETURN SYS_REFCURSOR IS
    c_locomotives SYS_REFCURSOR;
BEGIN
    OPEN c_locomotives FOR
        SELECT
            l.id AS LOCOMOTIVE_ID,
            l.name AS LOCOMOTIVE_NAME,
            m.name AS MAKE,
            rm.model AS MODEL,
            EXTRACT(YEAR FROM rs.startDate) AS SERVICE_YEAR,
            rm.numBogies AS NUM_BOGIES,
            rm.bogies AS BOGIES,
            lm.power AS POWER,
            rd.length AS LENGTH,
            rd.width AS WIDTH,
            rd.height AS HEIGHT,
            rd.weight AS WEIGHT,
            rm.maxSpeed AS MAX_SPEED,
            lm.operationalSpeed AS OPERATIONAL_SPEED,
            lm.traction AS TRACTION,
            CASE WHEN el.locomotiveModelID IS NOT NULL THEN 'Electric' ELSE 'Diesel' END AS LOCO_TYPE,
            NVL(el.voltage, 0) AS VOLTAGE,
            NVL(el.frequency, 0) AS FREQUENCY,
            rs.OperatorManagingVatNumber AS OPERATOR,
            (SELECT MIN(gaugeID) FROM RollingStockModel_Gauge rsg WHERE rsg.modelID = rm.id) AS GAUGE_ID,
            NVL(dl.fuelCapacity, 0) AS FUEL_CAPACITY,
            -- Location information
            CASE
                WHEN tl.TrainId IS NOT NULL THEN 'IN_TRANSIT'
                ELSE 'PARKED'
            END AS STATUS,
            NULL AS CURRENT_STATION_ID,
            NULL AS CURRENT_STATION_NAME,
            CASE WHEN tl.TrainId IS NOT NULL THEN t.id ELSE NULL END AS ROUTE_ID,
            NULL AS DESTINATION_STATION_ID,
            NULL AS DESTINATION_STATION_NAME,
            0 AS DISTANCE_FROM_START
        FROM Locomotive l
        JOIN LocomotiveModel lm ON l.locomotiveModelID = lm.locomotiveModelID
        JOIN RollingStock rs ON l.RollingStockId = rs.id
        JOIN RollingStockModel rm ON lm.RollingStockModelId = rm.id
        JOIN Manufacture m ON rm.manufacturerID = m.manufacturerID
        JOIN RollingStockDimension rd ON rm.RollingStockDimensionId = rd.id
        LEFT JOIN ElectricLocomotiveType el ON lm.locomotiveModelID = el.locomotiveModelID
        LEFT JOIN DieselLocomotiveType dl ON lm.locomotiveModelID = dl.locomotiveModelID
        LEFT JOIN Train_Locomotive tl ON l.id = tl.LocomotiveId
        LEFT JOIN Train t ON tl.TrainId = t.id
        WHERE tl.TrainId IS NULL  -- Only available locomotives (not assigned to trains)
        ORDER BY l.name;

    RETURN c_locomotives;
END;
/

-- Function to get available wagons with their current location/status
-- Returns wagons that are either PARKED or AVAILABLE for train assembly
CREATE OR REPLACE FUNCTION fn_get_available_wagons_for_route(
    p_route_start_station_id IN NUMBER
) RETURN SYS_REFCURSOR IS
    c_wagons SYS_REFCURSOR;
BEGIN
    OPEN c_wagons FOR
        SELECT
            w.wagonId AS WAGON_ID,
            wt.name AS WAGON_TYPE,
            m.name AS MAKE,
            rm.model AS MODEL,
            rd.length AS LENGTH,
            rd.width AS WIDTH,
            rd.height AS HEIGHT,
            rd.weight AS EMPTY_WEIGHT,
            wm.payLoad AS MAX_LOAD_CAPACITY,
            0 AS CURRENT_LOAD,
            (SELECT MIN(gaugeID) FROM RollingStockModel_Gauge rsg WHERE rsg.modelID = rm.id) AS GAUGE_ID,
            rs.OperatorManagingVatNumber AS OPERATOR,
            -- Location information
            CASE
                WHEN fw.FreightId IS NOT NULL THEN 'IN_TRANSIT'
                ELSE 'PARKED'
            END AS STATUS,
            NULL AS CURRENT_STATION_ID,
            NULL AS CURRENT_STATION_NAME,
            CASE WHEN fw.FreightId IS NOT NULL THEN f.TrainId ELSE NULL END AS ROUTE_ID,
            CASE WHEN fw.FreightId IS NOT NULL THEN f.destination ELSE NULL END AS DESTINATION_STATION_ID,
            NULL AS DESTINATION_STATION_NAME,
            0 AS DISTANCE_FROM_START
        FROM Wagon w
        JOIN RollingStock rs ON w.RollingStockId = rs.id
        JOIN RollingStockModel rm ON rs.RollingStockModelId = rm.id
        JOIN WagonModel wm ON rm.id = wm.RollingStockModelId
        JOIN WagonType wt ON wm.WagonTypeId = wt.typeId
        JOIN Manufacture m ON rm.manufacturerID = m.manufacturerID
        JOIN RollingStockDimension rd ON rm.RollingStockDimensionId = rd.id
        LEFT JOIN Freight_Wagon fw ON w.wagonId = fw.wagonId
        LEFT JOIN Freight f ON fw.FreightId = f.id
        WHERE fw.FreightId IS NULL  -- Only available wagons (not assigned to freight)
        ORDER BY wt.name, w.wagonId;

    RETURN c_wagons;
END;
/


