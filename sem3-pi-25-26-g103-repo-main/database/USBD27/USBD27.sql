CREATE OR REPLACE FUNCTION get_grain_wagons_used_in_all_trains (
    p_start_date IN DATE,
    p_end_date   IN DATE
) RETURN SYS_REFCURSOR
AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        WITH grain_wagons AS (
            SELECT w.wagonId
            FROM Wagon w
                     INNER JOIN WagonModel wm
                          ON w.RollingStockId = wm.RollingStockModelId
                     INNER JOIN WagonType wt
                          ON wm.WagonTypeId = wt.TypeId
            WHERE wt.name = 'Cereal wagon'
        ),
             trains_with_grain AS (
                 SELECT DISTINCT f.TrainId
                 FROM Freight f
                          INNER JOIN Freight_Wagon fw ON f.id = fw.FreightId
                          INNER JOIN Wagon w ON w.wagonId = fw.wagonId
                          INNER JOIN WagonModel wm ON w.RollingStockId = wm.RollingStockModelId
                          INNER JOIN WagonType wt ON wm.WagonTypeId = wt.TypeId
                          INNER JOIN Path p ON p.trainId = f.TrainId
                 WHERE wt.name = 'Cereal wagon'
                   AND p.departureTime BETWEEN p_start_date AND p_end_date
             ),
             totalgrain AS (
                 SELECT COUNT(*) AS total_trains
                 FROM trains_with_grain
             )
        SELECT gw.wagonId
        FROM grain_wagons gw
                 INNER JOIN Freight_Wagon fw ON gw.wagonId = fw.wagonId
                 INNER JOIN Freight f ON fw.FreightId = f.id
                 INNER JOIN trains_with_grain tg ON tg.TrainId = f.TrainId
                 CROSS JOIN totalgrain tt
        GROUP BY gw.wagonId, tt.total_trains
        HAVING COUNT(DISTINCT f.TrainId) = tt.total_trains;

    RETURN v_cursor;
END;
/
