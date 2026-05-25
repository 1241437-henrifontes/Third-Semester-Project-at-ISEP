DECLARE
    p_lineId Line.lineId%TYPE := 999;
    p_order LineSegment."order"%TYPE := 2;
    p_isElectrified LineSegment.isElectrified%TYPE := 'Y';
    p_maxWeight LineSegment.maxWeight%TYPE := 10000;
    p_length LineSegment.length%TYPE := 8000;
    p_numberOfTracks LineSegment.numberOfTracks%TYPE := 4;
    p_hasSiding Number := 0;
BEGIN
    add_line_segment(
            p_lineId,
            p_order,
            p_isElectrified,
            p_maxWeight,
            p_length,
            p_numberOfTracks,
            p_hasSiding
    );

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

