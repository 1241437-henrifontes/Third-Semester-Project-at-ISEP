DECLARE
    p_rsmId RollingStockModel.id%TYPE := 200;
    p_rsmModel RollingStockModel.model%TYPE := 'New Electric Locomotive Model';
    p_rsmNumBogies RollingStockModel.numBogies%TYPE := 3;
    p_rsmMaxSpeed RollingStockModel.maxSpeed%TYPE := 320;
    p_rsmBogies RollingStockModel.bogies%TYPE := 'Double';
    p_rsmRSDId RollingStockModel.RollingStockDimensionId%TYPE := 4;
    p_rsmManId RollingStockModel.manufacturerID%TYPE := 3;
    p_rsmMultipleGauges RollingStockModel.multipleGauges%TYPE := 'Y';
    p_locomotiveModelId LocomotiveModel.locomotiveModelID%TYPE := 200;
    p_locomotiveModelPower LocomotiveModel.power%TYPE := 9000;
    p_locomotiveModelOS LocomotiveModel.operationalSpeed%TYPE := 90;
    p_locomotiveModelTraction LocomotiveModel.traction%TYPE := 150;
    p_electricLocFrequency ElectricLocomotiveType.frequency%TYPE := 30;
    p_electricLocVoltage ElectricLocomotiveType.voltage%TYPE := 40000;
BEGIN
    addElectricLocomotiveModel(p_rsm_id => p_rsmId,
                               p_rsm_model => p_rsmModel,
                               p_rsm_numBogies => p_rsmNumBogies,
                               p_rsm_maxSpeed => p_rsmMaxSpeed,
                               p_rsm_bogies => p_rsmBogies,
                               p_rsmRSD_id => p_rsmRSDId,
                               p_rsmMan_id => p_rsmManId,
                               p_rsm_multipleGauges => p_rsmMultipleGauges,
                               p_locomotiveModel_id => p_locomotiveModelId,
                               p_locomotiveModel_power => p_locomotiveModelPower,
                               p_locomotiveModel_os => p_locomotiveModelOS,
                               p_locomotiveModel_traction => p_locomotiveModelTraction,
                               p_electricLoc_frequency => p_electricLocFrequency,
                               p_electricLoc_voltage => p_electricLocVoltage);
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
