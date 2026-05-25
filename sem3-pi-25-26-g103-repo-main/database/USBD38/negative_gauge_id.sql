DECLARE
p_gaugeWIDTH Gauge.gaugeWidth%TYPE:=-7;
    p_gaugeId Gauge.gaugeID%TYPE:=1438;
BEGIN
    add_gauge(p_gaugeId,p_gaugeWIDTH);
COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/