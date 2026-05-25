
CREATE OR REPLACE FUNCTION GetLocomotivesByModelAndOperator(
    p_locomotive_model_id IN NUMBER,
    p_operator_short_name IN VARCHAR2
) RETURN SYS_REFCURSOR
AS
    v_cur SYS_REFCURSOR;
BEGIN
OPEN v_cur FOR
SELECT
    l.name              AS locomotiveName,
    l.Id                AS locomotiveID,
    lm.model            AS modelName,
    rs.OPERATORMANAGINGVATNUMBER AS operatorVatNumber,
    c.shortName         AS operatorShortName
FROM Locomotive l
         INNER JOIN LocomotiveModel lm
                    ON l.locomotiveModelID = lm.locomotiveModelID
         INNER JOIN RollingStock rs
                    ON l.RollingStockId = rs.Id
         INNER JOIN Operator o
                    ON rs.OPERATORMANAGINGVATNUMBER = o.ManagingVatNumber
         INNER JOIN Company c
                    ON o.ManagingVatNumber = c.vatNumber
WHERE lm.locomotiveModelID = p_locomotive_model_id
  AND c.shortName = p_operator_short_name
ORDER BY l.Id;
RETURN v_cur;
END;
