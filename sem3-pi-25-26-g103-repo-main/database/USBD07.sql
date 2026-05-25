SELECT
    ls.segmentId,
    ls.lineId,
    l.name AS lineName,
    ls."order",
    ls.isElectrified,
    ls.maxWeight,
    ls.length,
    ls.numberOfTracks
FROM
    LineSegment ls
    INNER JOIN
    Line l ON ls.lineId = l.lineId
WHERE
    l.OwnerManagingVatNumber = 'PT503933813'