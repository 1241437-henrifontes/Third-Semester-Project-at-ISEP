 CREATE OR REPLACE PROCEDURE add_line_segment (
    p_lineId          IN Line.lineId%TYPE,
    p_order           IN LineSegment."order"%TYPE,
    p_isElectrified   IN LineSegment.isElectrified%TYPE,
    p_maxWeight       IN LineSegment.maxWeight%TYPE,
    p_length          IN LineSegment.length%TYPE,
    p_numberOfTracks  IN LineSegment.numberOfTracks%TYPE,
    p_hasSiding       IN NUMBER,
    p_sidingId        IN Siding.id%TYPE DEFAULT NULL,
    p_sidingPosition  IN Siding.position%TYPE DEFAULT NULL,
    p_sidingLength    IN Siding.length%TYPE DEFAULT NULL
)
    IS
    v_dummy     NUMBER;
    e_order_null_or_negative EXCEPTION;
    e_length_null_or_negative EXCEPTION;
    e_tracks_null_or_negative EXCEPTION;
    e_length_position_missing EXCEPTION;
    e_length_position_invalid EXCEPTION;
    e_hasSiding_invalid EXCEPTION;
    e_isElectrified_invalid EXCEPTION;
    e_maxWeight_invalid EXCEPTION;

BEGIN
    SELECT 1
    INTO v_dummy
    FROM Line
    WHERE lineId = p_lineId;

    IF p_order IS NULL OR p_order <= 0 THEN
        RAISE e_order_null_or_negative;
    END IF;

    IF p_length IS NULL OR p_length <= 0 THEN
        RAISE e_length_null_or_negative;
    END IF;

    IF p_numberOfTracks IS NULL OR p_numberOfTracks <= 0 THEN
        RAISE e_tracks_null_or_negative;
    END IF;

    IF p_maxWeight IS NULL OR p_maxWeight <= 0 THEN
        RAISE e_maxWeight_invalid;
    END IF;

    IF p_hasSiding != 0 AND p_hasSiding !=1 AND p_hasSiding IS NOT NULL THEN
        RAISE e_hasSiding_invalid;
    END IF;

    IF p_isElectrified IS NULL OR (p_isElectrified != 'Y' AND p_isElectrified != 'N') THEN
        RAISE e_isElectrified_invalid;
    END IF;



    IF p_hasSiding = 1 THEN
        BEGIN
            IF p_sidingPosition IS NULL OR p_sidingLength IS NULL OR p_sidingId IS NULL THEN
                RAISE e_length_position_missing;
            END IF;

            IF p_sidingLength <= 0 OR p_sidingPosition<=0 OR p_sidingID <=0 THEN
                RAISE e_length_position_invalid;
            END IF;

            INSERT INTO Siding (ID,position, length)
            VALUES (p_sidingId,p_sidingPosition, p_sidingLength);

        EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN
                RAISE_APPLICATION_ERROR(
                    -20107,
                    'Siding ID already exists.'
                );
        END;



    END IF;

    INSERT INTO LineSegment (
        lineId,
        "order",
        isElectrified,
        maxWeight,
        length,
        numberOfTracks,
        SidingId
    )
    VALUES (
               p_lineId,
               p_order,
               p_isElectrified,
               p_maxWeight,
               p_length,
               p_numberOfTracks,
               p_sidingId
           );




EXCEPTION
    WHEN e_order_null_or_negative THEN
        RAISE_APPLICATION_ERROR(
                -20100,
                'Segment order must be a positive number.'
        );
    WHEN e_length_null_or_negative THEN
        RAISE_APPLICATION_ERROR(
                -20101,
                'Segment length must be a positive number.'
        );
    WHEN e_tracks_null_or_negative THEN
        RAISE_APPLICATION_ERROR(
                -20102,
                'Number of tracks must be a positive number.'
        );
    WHEN e_length_position_missing THEN
        RAISE_APPLICATION_ERROR(
                -20103,
                'Siding position, length, and ID are required.'
        );
    WHEN e_length_position_invalid THEN
        RAISE_APPLICATION_ERROR(
                -20104,
                'Siding position, length, and ID must be positive numbers.'
        );
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(
                -20105,
                'Line does not exist.'
        );
    WHEN DUP_VAL_ON_INDEX THEN
        RAISE_APPLICATION_ERROR(
                -20106,
                'Segment order already exists for this line.'
        );
    WHEN e_hasSiding_invalid THEN
        RAISE_APPLICATION_ERROR(
                -20108,
                'p_hasSiding must be 0,1 or NULL.'
        );
    WHEN e_isElectrified_invalid THEN
        RAISE_APPLICATION_ERROR(
                -20109,
                'isElectrified must be ''Y'' or ''N''.'
        );
    WHEN e_maxWeight_invalid THEN
        RAISE_APPLICATION_ERROR(
                -20110,
                'maxWeight must be a positive number'
        );
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(
                -20999,
                'Unexpected error while adding line segment: ' || SQLERRM
        );
END;
/
