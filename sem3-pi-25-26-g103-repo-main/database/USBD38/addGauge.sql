CREATE OR REPLACE PROCEDURE add_gauge (
    p_gaugeWidth IN Gauge.gaugeWidth%TYPE,
    p_gaugeId IN Gauge.gaugeID%TYPE
)
IS
    e_null_Width EXCEPTION;
    e_negative_Width EXCEPTION;
    e_null_Id EXCEPTION;
    e_negative_Id EXCEPTION;
BEGIN

    IF p_gaugeWidth IS NULL THEN
        RAISE e_null_Width;
    END IF;

    IF p_gaugeWidth <= 0 THEN
       RAISE e_negative_width;
    END IF;

    IF p_gaugeId IS NULL THEN
       RAISE e_null_Id;
    END IF;

    IF p_gaugeId <= 0 THEN
       RAISE e_negative_Id;
    END IF;


INSERT INTO Gauge (gaugeID, gaugeWidth)
VALUES (p_gaugeId, p_gaugeWidth);

EXCEPTION
    WHEN e_null_Width THEN
        RAISE_APPLICATION_ERROR(
                -20010,
                'Gauge width cannot be NULL.'
        );
    WHEN e_negative_Width THEN
        RAISE_APPLICATION_ERROR(
                -20011,
                'Gauge width must be a positive value.'
        );
    WHEN e_null_Id THEN
        RAISE_APPLICATION_ERROR(
            -20012,
            'Gauge Id cannot be NULL.'
        );
    WHEN e_negative_Id THEN
        RAISE_APPLICATION_ERROR(
            -20013,
            'Gauge Id must be positive.'
        );

    WHEN DUP_VAL_ON_INDEX THEN
        RAISE_APPLICATION_ERROR(
                -20014,
                'Gauge Id or Width already exists.'
        );
WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(
                -20999,
                'Unexpected error while adding gauge: ' || SQLERRM
        );
END;
/
