
DECLARE
v_cur SYS_REFCURSOR;
    v_locomotiveName      Locomotive.name%TYPE;
    v_locomotiveID        Locomotive.Id%TYPE;
    v_modelName           LocomotiveModel.Model%TYPE;
    v_operatorVatNumber   RollingStock.OPERATORMANAGINGVATNUMBER%TYPE;
    v_operatorShortName   Company.shortName%TYPE;
BEGIN
    v_cur := GetLocomotivesByModelAndOperator(
        p_locomotive_model_id => 2,
        p_operator_short_name => 'Medway'
    );

    LOOP
FETCH v_cur INTO
            v_locomotiveName,
            v_locomotiveID,
            v_modelName,
            v_operatorVatNumber,
            v_operatorShortName;
        EXIT WHEN v_cur%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE(
            'Locomotive: ' || v_locomotiveName ||
            ', ID: '      || v_locomotiveID ||
            ', Model: '   || v_modelName ||
            ', Operator VAT: ' || v_operatorVatNumber ||
            ', Operator: ' || v_operatorShortName
        );
END LOOP;

CLOSE v_cur;
END;
/
