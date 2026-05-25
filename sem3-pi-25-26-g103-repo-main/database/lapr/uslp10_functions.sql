-- Functions for USLP10 - Train Scheduler

CREATE OR REPLACE FUNCTION fn_get_all_trains RETURN SYS_REFCURSOR IS
    c_trains SYS_REFCURSOR;
BEGIN
    OPEN c_trains FOR
        SELECT MIN(tl.LocomotiveId) AS LOCOMOTIVEID, t.id AS TRAINID
        FROM Train t
        LEFT JOIN Train_Locomotive tl ON t.id = tl.TrainId
        GROUP BY t.id;
    RETURN c_trains;
END;
/

CREATE OR REPLACE FUNCTION fn_get_locomotive_by_id(p_id IN NUMBER) RETURN SYS_REFCURSOR IS
    c_loco SYS_REFCURSOR;
BEGIN
    OPEN c_loco FOR
        SELECT 
            l.id AS ID, 
            l.name AS NAME, 
            m.name AS MAKE, 
            rm.model AS MODEL,
            EXTRACT(YEAR FROM rs.startDate) AS SERVICE,
            rm.numBogies AS NUMBOGIES,
            rm.bogies AS BOGIES,
            lm.power AS POWER,
            rd.length AS LENGTH,
            rd.width AS WIDTH,
            rd.height AS HEIGHT,
            rd.weight AS WEIGHT,
            rm.maxSpeed AS MAXSPEED,
            lm.operationalSpeed AS OPERATIONALSPEED,
            lm.traction AS TRACTION,
            CASE WHEN el.locomotiveModelID IS NOT NULL THEN 'Electric' ELSE 'Diesel' END AS TYPE,
            NVL(el.voltage, 0) AS VOLTAGE,
            NVL(el.frequency, 0) AS FREQUENCY,
            rs.OperatorManagingVatNumber AS OPERATOR,
            (SELECT MIN(gaugeID) FROM RollingStockModel_Gauge rsg WHERE rsg.modelID = rm.id) AS GAUGEID,
            NVL(dl.fuelCapacity, 0) AS GAS
        FROM Locomotive l
        JOIN LocomotiveModel lm ON l.locomotiveModelID = lm.locomotiveModelID
        JOIN RollingStock rs ON l.RollingStockId = rs.id
        JOIN RollingStockModel rm ON lm.RollingStockModelId = rm.id
        JOIN Manufacture m ON rm.manufacturerID = m.manufacturerID
        JOIN RollingStockDimension rd ON rm.RollingStockDimensionId = rd.id
        LEFT JOIN ElectricLocomotiveType el ON lm.locomotiveModelID = el.locomotiveModelID
        LEFT JOIN DieselLocomotiveType dl ON lm.locomotiveModelID = dl.locomotiveModelID
        WHERE l.id = p_id;
    RETURN c_loco;
END;
/

CREATE OR REPLACE FUNCTION fn_get_wagons_by_train(p_train_id IN NUMBER) RETURN SYS_REFCURSOR IS
    c_wagons SYS_REFCURSOR;
BEGIN
    OPEN c_wagons FOR
        SELECT DISTINCT
            w.wagonId AS WAGONID,
            wt.name AS TYPE,
            m.name AS MAKE,
            rm.model AS MODEL,
            rd.length AS LENGTH,
            rd.width AS WIDTH,
            rd.height AS HEIGHT,
            rd.weight AS EMPTYWEIGHT,
            wm.payLoad AS MAXCAPACITY,
            0 AS CURRENTLOAD,
            (SELECT MIN(gaugeID) FROM RollingStockModel_Gauge rsg WHERE rsg.modelID = rm.id) AS GAUGEID,
            rs.OperatorManagingVatNumber AS OPERATOR
        FROM Wagon w
        JOIN RollingStock rs ON w.RollingStockId = rs.id
        JOIN RollingStockModel rm ON rs.RollingStockModelId = rm.id
        JOIN WagonModel wm ON rm.id = wm.RollingStockModelId
        JOIN WagonType wt ON wm.WagonTypeId = wt.typeId
        JOIN Manufacture m ON rm.manufacturerID = m.manufacturerID
        JOIN RollingStockDimension rd ON rm.RollingStockDimensionId = rd.id
        JOIN Freight_Wagon fw ON w.wagonId = fw.wagonId
        JOIN Freight f ON fw.FreightId = f.id
        WHERE f.TrainId = p_train_id;
    RETURN c_wagons;
END;
/

CREATE OR REPLACE FUNCTION fn_get_all_facilities RETURN SYS_REFCURSOR IS
    c_fac SYS_REFCURSOR;
BEGIN
    OPEN c_fac FOR
        SELECT f.facilityId AS FACILITYID, f.name AS NAME, 
               NVL((SELECT MAX(ls.sidingId) FROM LineSegment ls WHERE ls.lineId IN (SELECT lineId FROM Line WHERE startFacilityId = f.facilityId OR endFacilityId = f.facilityId)), 0) AS SIDINGID
        FROM Facility f;
    RETURN c_fac;
END;
/

CREATE OR REPLACE FUNCTION fn_get_all_line_segments RETURN SYS_REFCURSOR IS
    c_seg SYS_REFCURSOR;
BEGIN
    OPEN c_seg FOR
        SELECT 
            ls.lineId AS LINEID, 
            ls."order" AS "ORDER", 
            ls.isElectrified AS ISELECTRIFIED, 
            ls.maxWeight AS MAXWEIGHT, 
            ls.length AS LENGTH, 
            ls.numberOfTracks AS NUMBEROFTRACKS, 
            ls.sidingId AS SIDINGID,
            l.startFacilityId AS STARTFACILITYID,
            l.endFacilityId AS ENDFACILITYID,
            fs.name AS STARTSTATIONNAME,
            fe.name AS ENDSTATIONNAME
        FROM LineSegment ls
        JOIN Line l ON ls.lineId = l.lineId
        JOIN Facility fs ON l.startFacilityId = fs.facilityId
        JOIN Facility fe ON l.endFacilityId = fe.facilityId;
    RETURN c_seg;
END;
/

CREATE OR REPLACE FUNCTION fn_get_all_pending_freights RETURN SYS_REFCURSOR IS
    c_freights SYS_REFCURSOR;
BEGIN
    OPEN c_freights FOR
        SELECT 
            f.id AS ID, 
            f.origin AS ORIGIN, 
            f.destination AS DESTINATION,
            fo.name AS ORIGIN_NAME,
            fd.name AS DEST_NAME,
            NVL((SELECT MAX(ls.sidingId) FROM LineSegment ls WHERE ls.lineId IN (SELECT lineId FROM Line WHERE startFacilityId = f.origin OR endFacilityId = f.origin)), 0) AS ORIGIN_SIDING,
            NVL((SELECT MAX(ls.sidingId) FROM LineSegment ls WHERE ls.lineId IN (SELECT lineId FROM Line WHERE startFacilityId = f.destination OR endFacilityId = f.destination)), 0) AS DEST_SIDING
        FROM Freight f
        JOIN Facility fo ON f.origin = fo.facilityId
        JOIN Facility fd ON f.destination = fd.facilityId;
    RETURN c_freights;
END;
/

CREATE OR REPLACE FUNCTION fn_get_all_paths RETURN SYS_REFCURSOR IS
    c_paths SYS_REFCURSOR;
BEGIN
    OPEN c_paths FOR
        SELECT trainId, departureTime, "order" as PATH_ORDER
        FROM Path;
    RETURN c_paths;
END;
/
