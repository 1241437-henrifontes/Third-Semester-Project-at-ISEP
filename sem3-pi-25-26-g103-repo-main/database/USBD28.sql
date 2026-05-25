BEGIN
FOR rec IN (
        SELECT
            l.name AS locomotiveName,
            l.Id AS locomotiveID,
            lm.model AS modelName,
            rs.OPERATORMANAGINGVATNUMBER AS OperatorvatNumber,
            c.shortName
        FROM
            Locomotive l
                INNER JOIN LocomotiveModel lm
                           ON l.locomotiveModelID = lm.locomotiveModelID
                INNER JOIN RollingStock rs
                           ON l.RollingStockId = rs.Id
                INNER JOIN Operator o
                           ON rs.OPERATORMANAGINGVATNUMBER = o.ManagingVatNumber
                INNER JOIN Company c
                            ON o.ManagingVatNumber = c.vatNumber
        WHERE
            lm.locomotiveModelID = 2
          AND c.shortName = 'Medway'
        ) LOOP
            DBMS_OUTPUT.PUT_LINE(
                    'Locomotive: ' || rec.locomotiveName ||
                    ', ID: ' || rec.locomotiveID ||
                    ', Model: ' || rec.modelName ||
                    ', Operator VAT: ' || rec.OperatorvatNumber ||
                    ', Operator: ' || rec.shortName
            );
END LOOP;
END;
/