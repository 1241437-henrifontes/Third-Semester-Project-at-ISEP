SELECT
    ls.lineId,
    l.name AS lineName,
    ls.numberOfTracks,
    COUNT(*) AS segmentCountByTrackType
FROM
    LineSegment ls
INNER JOIN
    Line l ON ls.lineId = l.lineId
WHERE
    ls.lineId = 5
GROUP BY
    ls.lineId, l.name, ls.numberOfTracks;

