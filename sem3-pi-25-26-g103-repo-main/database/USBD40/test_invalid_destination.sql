declare
    P_FREIGHT_ID  FREIGHT.ID %TYPE          := 9003;
    P_TRAIN_ID    FREIGHT.TRAINID %TYPE     := 5421;
    P_ORIGIN      FREIGHT.ORIGIN %TYPE      := 50;
    P_DESTINATION FREIGHT.DESTINATION %TYPE := 999;
    P_WAGON_IDS   WAGON_ID_LIST             := wagon_id_list('356 3 081');
begin
    SYSTEM.PRC_REGISTER_FREIGHT(
            P_FREIGHT_ID => P_FREIGHT_ID,
            P_TRAIN_ID => P_TRAIN_ID,
            P_ORIGIN => P_ORIGIN,
            P_DESTINATION => P_DESTINATION,
            P_WAGON_IDS => P_WAGON_IDS
    );
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -20003 THEN
            DBMS_OUTPUT.PUT_LINE('Caught expected error: ' || SQLERRM);
ELSE
            DBMS_OUTPUT.PUT_LINE('Caught unexpected error: ' || SQLERRM);
END IF;
end;
