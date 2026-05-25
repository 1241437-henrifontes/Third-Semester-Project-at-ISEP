SELECT
    w.wagonId,
    wt.model AS wagon_model,
    wt.type AS wagon_type,
    g.gaugeWidth,
    w.service
FROM Wagon w
    INNER JOIN WagonType wt ON w.WagonTypeId = wt.typeId
    INNER JOIN WagonType_Gauge wg ON wt.typeId = wg.WagonTypeId
    INNER JOIN Gauge g ON wg.gaugeID = g.gaugeID
WHERE w.OperatorvatNumber = 'PT509017800'
  AND wt.type = 'Container wagon (max 40'''' HC)'
  AND g.gaugeWidth = 1668;